package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;

/**
 * @see AccessType#READ_ONLY
 */
public class ReadOnlyMemcachedNaturalIdRegionAccessStrategy 
    extends AbstractNoLockMemcachedRegionAccessStrategy<MemcachedNaturalIdRegion>
    implements NaturalIdRegionAccessStrategy {

    public ReadOnlyMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region) {
        super(region);
    }

    /**
     * {@inheritDoc}</br>
     * Data is only added to the cache when loaded from the database
     * @return false, no change to the cache
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#putFromLoad(Object, Object, long, Object, boolean)
     */
    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        return false;
    }
 
    /**
     * {@inheritDoc}</br>
     * Data is only added to the cache when loaded from the database
     * @return false, no change to the cache
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#putFromLoad(Object, Object, long, Object, boolean)
     */
    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) {
        return false;
    }
    
    /**
     * {@inheritDoc}</br>
     * Data is only added to the cache when loaded from the database
     * @return false, no change to the cache
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#putFromLoad(Object, Object, long, Object, boolean)
     */
    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        return false;
    }
 
    /**
     * {@inheritDoc}</br>
     * Data is only added to the cache when loaded from the database
     * @return false, no change to the cache
     * @see org.hibernate.cache.spi.access.RegionAccessStrategy#putFromLoad(Object, Object, long, Object, boolean)
     */
    @Override
    public boolean update(Object key, Object value) {
        return false;
    }

}
