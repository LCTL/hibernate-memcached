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
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;
import com.googlecode.hibernate.memcached.region.MemcachedRegion;
import com.googlecode.hibernate.memcached.region.MemcachedRegionComponentFactory;

/**
 * Implements {@link EntityRegionAccessStrategy}.
 * 
 * @author kcarlson
 * 
 * @see AccessType#NONSTRICT_READ_WRITE
 */
public class NonStrictReadWriteMemcachedEntityRegionAccessStrategy 
    extends AbstractNoLockMemcachedRegionAccessStrategy<MemcachedEntityRegion> 
    implements EntityRegionAccessStrategy {

    public NonStrictReadWriteMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion region) {
        super(region);
    }

    /**
     * {@inheritDoc}
     * @return true, the cache was changed
     */
    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        MemcachedRegion region = getRegion();
        MemcachedRegionComponentFactory componentFactory = region.createComponentFactory();
        HibernateMemcachedClient client = componentFactory.createMemcacheClient();
        
        return client.set(toKey(key), region.getTimeout(), value);
    }

    /**
     * {@inheritDoc}
     * @return true, the cache was changed
     */
    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
        MemcachedRegion region = getRegion();
        MemcachedRegionComponentFactory componentFactory = region.createComponentFactory();
        HibernateMemcachedClient client = componentFactory.createMemcacheClient();
        
        return client.set(toKey(key), region.getTimeout(), value);
    }

    /**
     * {@inheritDoc}</br>
     * Only want to insert after the transaction completes.
     * This cache is asynchronous hence a no-op.
     * @return false, no change to the cache
     */
    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    /**
     * {@inheritDoc}</br>
     * Only want to update after the transaction completes.
     * This cache is asynchronous hence a no-op.
     * @return false, no change to the cache
     */
    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
        // remove on update?
        return false;
    }
}
