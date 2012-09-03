package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;

import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;

/**
 * @see AccessType#TRANSACTIONAL
 */
public class TransactionalMemcachedNaturalIdRegionAccessStrategy 
    extends AbstractMemcachedRegionAccessStrategy<MemcachedNaturalIdRegion> 
    implements NaturalIdRegionAccessStrategy {

    public TransactionalMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region) {
        super(region);
        throw new UnsupportedOperationException("TransactionalMemcachedNaturalIdRegionAccessStrategy not yet implemented");
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

	@Override
	public Object get(Object key, long txTimestamp) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean putFromLoad(Object key, Object value, long txTimestamp,
			Object version, boolean minimalPutOverride) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public SoftLock lockItem(Object key, Object version) throws CacheException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Object key) throws CacheException {
		// TODO Auto-generated method stub
		
	}

}
