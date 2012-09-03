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

import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;

/**
 *
 * @author kcarlson
 * 
 * @see AccessType#TRANSACTIONAL
 */
public class TransactionalMemcachedEntityRegionAccessStrategy
    extends AbstractMemcachedRegionAccessStrategy<MemcachedEntityRegion> 
    implements EntityRegionAccessStrategy {

    public TransactionalMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion region) {
        super(region);
        throw new UnsupportedOperationException("TransactionalMemcachedEntityRegionAccessStrategy not yet implemented");

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
	public boolean insert(Object key, Object value, Object version)
			throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean afterInsert(Object key, Object value, Object version)
			throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Object key, Object value, Object currentVersion,
			Object previousVersion) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean afterUpdate(Object key, Object value, Object currentVersion,
			Object previousVersion, SoftLock lock) throws CacheException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remove(Object key) throws CacheException {
		// TODO Auto-generated method stub
		
	}

}
