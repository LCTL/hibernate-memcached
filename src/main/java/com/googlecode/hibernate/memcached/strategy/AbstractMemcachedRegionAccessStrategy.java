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
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.region.MemcachedRegion;

/**
 *
 * @author kcarlson
 */
public abstract class AbstractMemcachedRegionAccessStrategy<T extends MemcachedRegion>
    implements RegionAccessStrategy {

    private T region;
    private Settings settings;
    
    public AbstractMemcachedRegionAccessStrategy(T region, Settings settings) {
        this.region = region;
        this.settings = settings;
    }

    @Override
    public void evict(Object key) throws CacheException {
        region.delete(String.valueOf(key));
    }

    @Override
    public void evictAll() throws CacheException {
        region.clear(); // does this really mean the whole cache or just the region?
    }

    public T getRegion() {
        return region;
    }

    @Override
    public SoftLock lockRegion() throws CacheException {
        throw new UnsupportedOperationException("Memcached does not support region locking");
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
        return putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
    }

    @Override
    public void remove(Object key) throws CacheException {
        evict(key);
    }

    @Override
    public void removeAll() throws CacheException {
        if (!region.clear()) {
            //UnsupportedOperationException?
            throw new CacheException("Memcached does not support region level data eviction");
        }
    }

    @Override
    public void unlockRegion(SoftLock lock) throws CacheException {
        throw new UnsupportedOperationException("Memcached does not support region locking");
    }
    
}
