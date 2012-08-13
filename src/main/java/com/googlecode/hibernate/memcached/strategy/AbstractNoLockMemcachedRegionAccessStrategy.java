package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.MemcachedRegion;

public class AbstractNoLockMemcachedRegionAccessStrategy<T extends MemcachedRegion> 
    extends AbstractMemcachedRegionAccessStrategy<T> {

    public AbstractNoLockMemcachedRegionAccessStrategy(T region, Settings settings) {
        super(region, settings);
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        return getRegion().get(String.valueOf(key));
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && getRegion().get(String.valueOf(key)) != null) {
            return false;
        } else {
            getRegion().set(String.valueOf(key), value);
            return true;
        }
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
    }

}
