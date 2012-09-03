package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.region.MemcachedRegion;
import com.googlecode.hibernate.memcached.region.MemcachedRegionComponentFactory;

/**
 * An abstract {@link RegionAccessStrategy} that does not support key locking.
 * 
 * @param <T> the underling {@link MemcachedRegion} implementation type
 */
public class AbstractNoLockMemcachedRegionAccessStrategy<T extends MemcachedRegion> 
    extends AbstractMemcachedRegionAccessStrategy<T> {

    /**
     * Creates a new access strategy for the given region.
     * 
     * @param region the region this strategy grants access to
     */
    public AbstractNoLockMemcachedRegionAccessStrategy(T region) {
        super(region);
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        return getRegion().createComponentFactory().createMemcacheClient().get(toKey(key));
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        MemcachedRegion region = getRegion();
        MemcachedRegionComponentFactory componentFactory = region.createComponentFactory();
        HibernateMemcachedClient client = componentFactory.createMemcacheClient();
        
        if (minimalPutOverride && client.get(toKey(key)) != null) {
            return false;
        } else {
            return client.set(toKey(key), region.getTimeout(), value);
        }
    }
    
    @Override
    public void remove(Object key) throws CacheException {
        evict(key);
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
    }

}
