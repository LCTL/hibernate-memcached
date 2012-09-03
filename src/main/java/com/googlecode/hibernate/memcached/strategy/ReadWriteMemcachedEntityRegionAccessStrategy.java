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
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;
import com.googlecode.hibernate.memcached.region.MemcachedRegion;

/**
 *
 * @author kcarlson
 * 
 * @see AccessType#READ_WRITE
 */
public class ReadWriteMemcachedEntityRegionAccessStrategy 
    extends AbstractReadWriteMemcachedAccessStrategy<MemcachedEntityRegion>
    implements EntityRegionAccessStrategy {

    private static final Logger log = LoggerFactory.getLogger(ReadWriteMemcachedEntityRegionAccessStrategy.class);

    public ReadWriteMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion region) {
        super(region, region.getCacheDataDescription());
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        LockedExecution<Boolean> execution = new LockedExecution<Boolean>(value, version) {
            private boolean success = false;

            @Override
            public void exec(String objectKey, HibernateMemcachedClient client, MemcachedRegion region) {
                Object value = args[0];
                Object version = args[1];

                Lockable item = (Lockable) client.get(objectKey);
                if (item == null) {
                    success = client.set(objectKey, region.getTimeout(), new Item(value, version, region.nextTimestamp()));
                }
            }

            @Override
            public Boolean result() {
                return success;
            }
        };

        String objectKey = toKey(key);
        wrapWithWriteLock(objectKey, execution);
    
        if (!execution.result()) {
            log.warn("Could not afterInsert item for key {}", objectKey);
        }
    
        return execution.result();
    }

    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
        LockedExecution<Boolean> execution = new LockedExecution<Boolean>(value, currentVersion, previousVersion, lock) {
            private boolean success = false;

            @Override
            public void exec(String objectKey, HibernateMemcachedClient client, MemcachedRegion region) {
                Object value = args[0];            Object currentVersion = args[1];
                Object previousVersion = args[2];  SoftLock lock = (SoftLock) args[3];

                Lockable item = (Lockable) client.get(objectKey);
                boolean unlockable = item != null && item.isUnlockable(lock);
                if (unlockable) {
                    Lock lockItem = (Lock) item;
                    if (lockItem.wasLockedConcurrently()) {
                        success = decrementLock(client, objectKey, lockItem);
                    } else {
                        success = client.set(objectKey, region.getTimeout(), new Item(value, currentVersion, region.nextTimestamp()));
                    }
                } else {
                    success = handleLockExpiry(client, objectKey, null);
                }
            }

            @Override
            public Boolean result() {
                return success;
            }
        };

        String objectKey = toKey(key);
        wrapWithWriteLock(objectKey, execution);
    
        if (!execution.result()) {
            log.warn("Could not afterUpdate item for key {}", objectKey);
        }
    
        return execution.result();
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

    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
        return false;
    }
   
}
