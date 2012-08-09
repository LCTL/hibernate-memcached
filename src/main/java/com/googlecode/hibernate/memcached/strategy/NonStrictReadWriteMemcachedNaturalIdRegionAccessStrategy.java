package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;

public class NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy 
    extends AbstractNoLockMemcachedRegionAccessStrategy<MemcachedNaturalIdRegion>
    implements NaturalIdRegionAccessStrategy {

    public NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region, Settings settings) {
        super(region, settings);
    }

    /**
     * {@inheritDoc}
     * @return true, the cache was changed
     */
    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        getRegion().getCache().put(key, value);
        return true;
    }

    /**
     * {@inheritDoc}
     * @return true, the cache was changed
     */
    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        getRegion().getCache().put(key, value);
        return true;
    }

    /**
     * {@inheritDoc}</br>
     * Only want to insert after the transaction completes.
     * This cache is asynchronous hence a no-op.
     * @return false, no change to the cache
     */
    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        return false;
    }

    /**
     * {@inheritDoc}</br>
     * Only want to update after the transaction completes.
     * This cache is asynchronous hence a no-op.
     * @return false, no change to the cache
     */
    @Override
    public boolean update(Object key, Object value) throws CacheException {
        return false;
    }
}
