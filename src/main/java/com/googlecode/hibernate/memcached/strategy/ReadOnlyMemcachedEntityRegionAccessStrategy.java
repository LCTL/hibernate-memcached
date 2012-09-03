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

import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;

/**
 *
 * @author kcarlson
 * 
 * @see AccessType#READ_ONLY
 */
public class ReadOnlyMemcachedEntityRegionAccessStrategy
    extends AbstractNoLockMemcachedRegionAccessStrategy<MemcachedEntityRegion> 
    implements EntityRegionAccessStrategy {

    public ReadOnlyMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion region) {
        super(region);
    }
 
    /**
     * {@inheritDoc}</br>
     * Data is only added to the cache when loaded from the database
     * @return false, no change to the cache
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#putFromLoad(Object, Object, long, Object, boolean)
     */
    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        return false;
    }
 
    /**
     * {@inheritDoc}</br>
     * Data is only added to the cache when loaded from the database
     * @return false, no change to the cache
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#putFromLoad(Object, Object, long, Object, boolean)
     */
    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
        return false;
    }
    
    /**
     * {@inheritDoc}</br>
     * Data is only added to the cache when loaded from the database
     * @return false, no change to the cache
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#putFromLoad(Object, Object, long, Object, boolean)
     */
    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }
 
    /**
     * {@inheritDoc}</br>
     * Data is only added to the cache when loaded from the database
     * @return false, no change to the cache
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#putFromLoad(Object, Object, long, Object, boolean)
     */
    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) {
        return false;
    }
    
}
