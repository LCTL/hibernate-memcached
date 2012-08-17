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
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionPropertiesHolder;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.concurrent.keylock.ReadWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.strategy.clear.ClearStrategy;
import com.googlecode.hibernate.memcached.strategy.clear.MemcachedRegionClearStrategy;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;

/**
 *
 * @author kcarlson
 */
public class AbstractMemcachedRegion 
    implements MemcachedRegion {

    public static final Logger log = LoggerFactory.getLogger(AbstractMemcachedRegion.class);
    
    // make private?
    public static final Integer DOGPILE_TOKEN = 0;
    
    // final?
    private HibernateMemcachedClient client;
    protected MemcachedRegionPropertiesHolder properties;
    private Settings settings; 
    
    private final ClearStrategy clearStrategy;
    private ReadWriteKeyLockProvider lockProvider;
    
    AbstractMemcachedRegion(HibernateMemcachedClient client, MemcachedRegionPropertiesHolder properties, Settings settings) {
        this.client = client;
        this.properties = properties;
        this.settings = settings;
        
        this.clearStrategy = properties.getClearStrategy();
        this.lockProvider = properties.getReadWriteKeyLockProvider();
    }
    
    // Region methods
    
    @Override
    public String getName() {
        return properties.getName();
    }

    @Override
    public void destroy() throws CacheException {
        //the client is shared by default with all cache instances, so don't shut it down?
        shutdown();
    }

    @Override
    public boolean contains(Object key) {
        // change sig?
        return get(String.valueOf(key)) != null;
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
        return (HashMap<?,?>) new HashMap(0);
    }

    @Override
    public long nextTimestamp() {
        // why not keep in millis?
        return System.currentTimeMillis() / 100;
    }

    @Override
    public int getTimeout() {
        // is this really what this method should return?
        return getCacheTimeSeconds();
    }
    
    // HibernateMemcachedClient methods 
    
    @Override
    public Object get(String key) {
        String objectKey = toKey(key); // refactor...
        Map<String, Object> result = getMulti(key);
        return result == null ? null : result.get(objectKey);
    }

    @Override
    public Map<String, Object> getMulti(String... keys) {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = toKey(keys[i]);
        }
        
        if (isDogpilePreventionEnabled()) {
            return getMultiUsingDogpilePrevention(keys);
        } else {
            log.debug("Memcache.getMulti({})", keys);
            return client.getMulti(keys);
        }
        
    }

    @Override
    public boolean set(String key, int cacheTimeSeconds, Object o) {
        String objectKey = toKey(key);
        int cacheTime = setDogpileKey(objectKey, cacheTimeSeconds);
        log.debug("Memcache.set({})", objectKey);
        return client.set(objectKey, cacheTime, o);
    }

    @Override
    public boolean set(String key, Object o) {
        return set(key, getCacheTimeSeconds(), o);
    }
    
    @Override
    public boolean add(String key, int cacheTimeSeconds, Object o) {
        String objectKey = toKey(key);
        int cacheTime = setDogpileKey(objectKey, cacheTimeSeconds);
        log.debug("Memcache.add({})", objectKey);
        return client.add(objectKey, cacheTime, o);
    }

    @Override
    public boolean add(String key, Object o) {
        return add(key, getCacheTimeSeconds(), o);
    }
    
    @Override
    public void delete(String key) {
        client.delete(toKey(key));
    }

    @Override
    public long incr(String key, long factor, long startingValue) {
        return client.incr(toKey(key), factor, startingValue);
    }

    @Override
    public long decr(String key, long by, long startingValue) {
        return client.decr(toKey(key), by, startingValue);
    }

    @Override
    public void shutdown() {
        client.shutdown();
    }

    // Other Methods
    

    public boolean clear() throws CacheException {
        return clearStrategy.clear();
    }
    
    // Properties delegators (allow setting?)
    
    public boolean isDogpilePreventionEnabled() {
        return properties.isDogpilePreventionEnabled();
    }
    
    public KeyStrategy getKeyStrategy() {
        return properties.getKeyStrategy();
    }

	public String getReadLockKeyPrefix() {
		return properties.getReadLockKeyPrefix();
	}

	public String getWriteLockKeyPrefix() {
		return properties.getWriteLockKeyPrefix();
	}

	public boolean isClearSupported() {
		return properties.isClearSupported();
	}

	public String getClearIndexKeyPrefix() {
		return properties.getClearIndexKeyPrefix();
	}

	public int getCacheTimeSeconds() {
		return properties.getCacheTimeSeconds();
	}

	public double getDogpilePreventionExpirationFactor() {
		return properties.getDogpilePreventionExpirationFactor();
	}

	public String getNamespaceSeparator() {
		return properties.getNamespaceSeparator();
	}

    // ReadWriteKeyLockProvider methods
    
    @Override
    public boolean acquireReadLock(String key) {
        return lockProvider.acquireReadLock(key);
    }

    @Override
    public boolean releaseReadLock(String key) {
        return lockProvider.releaseReadLock(key);
    }

    @Override
    public boolean acquireWriteLock(String key) {
        return lockProvider.acquireWriteLock(key);
    }

    @Override
    public boolean releaseWriteLock(String key) {
        return lockProvider.releaseWriteLock(key);
    }

    // Object methods

    public String toString() {
        return "Memcached (" + getName() + ")";
    }
    

    // private and protected methods
    
    protected Settings getSettings() {
        return settings;
    }
    
    private int setDogpileKey(String objectKey, int cacheTime) {
        if (isDogpilePreventionEnabled()) {
            String dogpileKey = dogpileTokenKey(objectKey);
            log.debug("Dogpile prevention enabled, setting token and adjusting object cache time. Key: [{}]", dogpileKey);
            client.set(dogpileKey, getCacheTimeSeconds(), DOGPILE_TOKEN);
            cacheTime = (int) (cacheTime * getDogpilePreventionExpirationFactor());
        }
        
        return cacheTime;
    }
    
    private String toKey(Object key) {
        return getKeyStrategy().toKey(getName(), clearStrategy.getClearIndex(), key);
    }
    
    private Map<String, Object> getMultiUsingDogpilePrevention(String ... objectKeys) {
        Map<String, Object> multi;

        String[] allKeys = new String[objectKeys.length*2];
        for (int i = 0; i < objectKeys.length; i++) {
            allKeys[i] = objectKeys[i];
            allKeys[i+1] = dogpileTokenKey(objectKeys[i]);
            log.debug("Checking dogpile key: [{}]", allKeys[i+1]);
        }
        
        log.debug("Memcache.getMulti({})", allKeys);
        multi = client.getMulti(allKeys);

        for (int i = 0; i < objectKeys.length; i++) {
            if ((multi == null) || (multi.get(allKeys[i+1]) == null)) {
                log.debug("Dogpile key ({}) not found updating token and returning null", allKeys[i+1]);
                client.set(allKeys[i+1], getCacheTimeSeconds(), DOGPILE_TOKEN);
                multi.put(allKeys[i], null);
            }
        }

        return multi;
    }
    
    // TODO: move to properties
    private String dogpileTokenKey(String objectKey) {
        return objectKey + ".dogpileTokenKey";
    }

}
