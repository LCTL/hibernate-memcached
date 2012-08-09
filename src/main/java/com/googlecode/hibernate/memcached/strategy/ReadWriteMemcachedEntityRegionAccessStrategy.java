/*
 * -----------------------------------------------------------------------------
 * Copyright (C) 2008-2011 by Bloo AB
 * SWEDEN, e-mail: info@bloo.se
 *
 * This program may be used and/or copied only with the written permission
 * from Bloo AB, or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the program
 * has been supplied.
 *
 * All rights reserved.
 *
 * -----------------------------------------------------------------------------
 */
package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;

/**
 *
 * @author kcarlson
 */
public class ReadWriteMemcachedEntityRegionAccessStrategy 
    extends AbstractReadWriteMemcachedAccessStrategy<MemcachedEntityRegion>
    implements EntityRegionAccessStrategy {

    public ReadWriteMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion region, Settings settings) {
        super(region, settings, region.getCacheDataDescription());
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        getRegion().getCache().lock(key);
        try {
            Lockable item = (Lockable) getRegion().getCache().get(key);
            if (item == null) {
                getRegion().getCache().put(key, new Item(value, version, getRegion().nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            getRegion().getCache().unlock(key);
        }
    }

    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
        //what should we do with previousVersion here?
        getRegion().getCache().lock(key);
        try {
            Lockable item = (Lockable) getRegion().getCache().get(key);
            boolean unlockable = item != null && item.isUnlockable(lock);
            if (unlockable) {
                Lock lockItem = (Lock) item;
                if (lockItem.wasLockedConcurrently()) {
                    decrementLock(key, lockItem);
                    return false;
                } else {
                    getRegion().getCache().put(key, new Item(value, currentVersion, getRegion().nextTimestamp()));
                    return true;
                }
            } else {
                super.handleLockExpiry(key, null);
                return false;
            }
        } finally {
            getRegion().getCache().unlock(key);
        }
    }

    /**
     * {@inheritDoc}</br>
     * Only want to insert after the transaction completes.
     * This cache is asynchronous hence a no-op.
     */
    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
        return false;
    }
   
}
