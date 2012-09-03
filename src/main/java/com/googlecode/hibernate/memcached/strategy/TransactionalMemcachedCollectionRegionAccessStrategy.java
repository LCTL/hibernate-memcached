/* Copyright 2008 Ray Krueger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;

import com.googlecode.hibernate.memcached.region.MemcachedCollectionRegion;

/**
 *
 * @author kcarlson
 * 
 * @see AccessType#TRANSACTIONAL
 */
public class TransactionalMemcachedCollectionRegionAccessStrategy
    extends AbstractMemcachedRegionAccessStrategy<MemcachedCollectionRegion> 
    implements CollectionRegionAccessStrategy {

    public TransactionalMemcachedCollectionRegionAccessStrategy(MemcachedCollectionRegion region) {
        super(region);
        throw new UnsupportedOperationException("TransactionalMemcachedCollectionRegionAccessStrategy not yet implemented");

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
