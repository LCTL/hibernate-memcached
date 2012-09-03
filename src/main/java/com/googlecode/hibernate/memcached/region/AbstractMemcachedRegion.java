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
package com.googlecode.hibernate.memcached.region;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedRegionClient;
import com.googlecode.hibernate.memcached.concurrent.keylock.ConcurrentReadBlockingWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.concurrent.keylock.MemcachedReadWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.concurrent.keylock.UnBlockedReadBlockedWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.strategy.clear.MemcachedRegionClearStrategy;

/**
 * An abstract implementation of the {@link MemcachedRegion} interface.
 * <p>
 * This implementation supports region clearing
 * ({@link MemcachedRegionClearStrategy}) and dogpile prevention (stampeding
 * herd prevention?) if enabled in the settings. It also supports key locking
 * ({@link MemcachedReadWriteKeyLockProvider}), though locks must be enforced
 * by clients.
 * <p>
 * Clients of this region can access the underling Memcached client and lock
 * provider using the {@link #createComponentFactory()} method.
 * <p>
 * All required methods are implemented, leave this class abstract?
 *
 * @author kcarlson
 * 
 * @see HibernateMemcachedRegionClient
 * @see ConcurrentReadBlockingWriteKeyLockProvider
 */
public abstract class AbstractMemcachedRegion implements MemcachedRegion {

    public static final Logger log = LoggerFactory.getLogger(AbstractMemcachedRegion.class);
    
    private final HibernateMemcachedClient client;
    private final MemcachedRegionSettings settings;
    
    /**
     * Creates a {@link MemcachedRegion} with the given 
     * {@link HibernateMemcachedClient} and {@link MemcachedRegionSettings}.
     * 
     * @param client   the client used to access Memcached
     * @param settings the settings for this region
     */
    public AbstractMemcachedRegion(HibernateMemcachedClient client, MemcachedRegionSettings settings) {
        this.client = client;
        this.settings = settings;
    }
    
    // MemcachedRegion Methods
    
    @Override
    public boolean clear() throws CacheException {
        if (settings.isClearSupported()) {
            return settings.getClearStrategy().clear();
        } else {
            return false;
        }
    }
    
    @Override
    public MemcachedRegionSettings getSettings() {
        return settings;
    }
    
    @Override
    public MemcachedRegionComponentFactory createComponentFactory() {
        // It might be worth implementing a component factory that always returns
        // the same client and lock provider when clearing is not enabled.
        return new MemcachedRegionComponentFactory() {
            private long clearIndex = settings.getClearStrategy().getClearIndex();

            @Override
            public HibernateMemcachedClient createMemcacheClient() {
                return new HibernateMemcachedRegionClient(client, settings, clearIndex);
            }

            @Override
            public MemcachedReadWriteKeyLockProvider createMemcachedReadWriteKeyLockProvider() {
                return new UnBlockedReadBlockedWriteKeyLockProvider(client, settings, clearIndex);
            }
        };
    }
    
    // Region methods
    
    @Override
    public String getName() {
        return settings.getName();
    }

    @Override
    public void destroy() throws CacheException {
        // the client is shared by default with all cache instances, so don't shut it down?
    }

    @Override
    public boolean contains(Object key) {
        return createComponentFactory().createMemcacheClient().get(toKey(key)) != null;
    }

    @Override
    public long getSizeInMemory() {
        return -1;
    }

    @Override
    public long getElementCountInMemory() {
        return -1;
    }

    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    @Override
    public Map<?,?> toMap() {
        return new HashMap<Object,Object>(0);
    }

    @Override
    public long nextTimestamp() {
        // why not keep in millis? // is nextTimestamp == now?
        return System.currentTimeMillis() / 100;
    }

    @Override
    public int getTimeout() {
        return settings.getCacheTimeSeconds();
    }
    
    // KeyStrategy Methods
    
    @Override
    public String toKey(Object o) {
        return settings.getKeyStrategy().toKey(o);
    }

    // Object methods

    @Override
    public String toString() {
        return "MemcachedRegion (" + getName() + ")";
    }
   
}
