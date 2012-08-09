package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;

public class ReadWriteMemcachedNaturalIdRegionAccessStrategy 
    extends AbstractReadWriteMemcachedAccessStrategy<MemcachedNaturalIdRegion> 
    implements NaturalIdRegionAccessStrategy {

    public ReadWriteMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region, Settings settings, CacheDataDescription cacheDataDescription) {
        super(region, settings, cacheDataDescription);
        throw new UnsupportedOperationException("ReadWriteMemcachedNaturalIdRegionAccessStrategy not yet implemented");
    }

	public boolean insert(Object key, Object value) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean afterInsert(Object key, Object value) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(Object key, Object value) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean afterUpdate(Object key, Object value, SoftLock lock)
			throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

}
