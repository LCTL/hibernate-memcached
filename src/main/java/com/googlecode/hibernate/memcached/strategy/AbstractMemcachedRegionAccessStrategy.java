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
 * An abstract implementation of the {@link RegionAccessStrategy} interface.
 * 
 * @author kcarlson
 *
 * @param <T> the underling {@link MemcachedRegion} implementation type
 */
public abstract class AbstractMemcachedRegionAccessStrategy<T extends MemcachedRegion>
    implements RegionAccessStrategy {

    private T region;
    private Settings settings;
    
    /**
     * Creates a new access strategy for the given region.
     * 
     * @param region the region this strategy grants access to
     */
    public AbstractMemcachedRegionAccessStrategy(T region) {
        this.region = region;
        this.settings = region.getSettings().getHibernateSettings();
    }

    @Override
    public void evict(Object key) throws CacheException {
        region.createComponentFactory().createMemcacheClient().delete(toKey(key));
    }

    @Override
    public void evictAll() throws CacheException {
        removeAll(); // does this really mean the whole cache or just the region?
    }

    /**
     * Implements method declared by many interfaces that inherit from this
     * class.
     * 
     * @return the underlying region
     */
    public T getRegion() {
        return region;
    }

    @Override
    public SoftLock lockRegion() throws CacheException {
        //throw new UnsupportedOperationException("Region level locking is not supported.");
        return null;
    }
    
    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
        Settings settings = getRegion().getSettings().getHibernateSettings();
        return putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
    }
    
    @Override
    public void removeAll() throws CacheException {
        /*if (!getRegion().clear()) {
            throw new UnsupportedOperationException("Region level data eviction is not supported.");
        }*/
    }
    
    @Override
    public void unlockRegion(SoftLock lock) throws CacheException {
        //throw new UnsupportedOperationException("Region level locking is not supported.");
    }
    
    /**
     * Convenience method for turning an object into a key.
     * 
     * @see MemcachedRegion#toKey(Object)
     */
    protected String toKey(Object o) {
        return region.toKey(o);
    }
}
