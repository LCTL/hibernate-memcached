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
    
    private final String clearIndexKey;
    
    AbstractMemcachedRegion(HibernateMemcachedClient client, MemcachedRegionPropertiesHolder properties, Settings settings) {
        this.client = client;
        this.properties = properties;
        this.settings = settings;
        
        //if (getName() == null) { setName(""); }
        
        this.clearIndexKey = new StringBuilder()
            .append(getClearIndexKeyPrefix())
            .append(getNamespaceSeparator())
            .append(getName().replaceAll("\\s", ""))
            .toString();
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
    public void incr(String key, int factor, int startingValue) {
        client.incr(toKey(key), factor, startingValue);
    }

    @Override
    public void decr(String key, int by, int startingValue) {
        client.decr(toKey(key), by, startingValue);
    }

    @Override
    public void shutdown() {
        client.shutdown();
    }

    // Other Methods
    
    /**
     * Clear functionality is disabled by default.
     * Read this class's javadoc for more detail.
     *
     * @throws CacheException
     * @see com.googlecode.hibernate.memcached.MemcachedCache
     */
    public void clear() throws CacheException {
        if (isClearSupported()) {
            incr(clearIndexKey, 1, 1);
        }
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
        return getKeyStrategy().toKey(getName(), getClearIndex(), key);
    }
    
    private long getClearIndex() {
        long index = 0L;

        if (isClearSupported()) {
            Object value = client.get(clearIndexKey);
            if (value != null) {
                if (value instanceof String) {
                    index = Long.valueOf((String) value);
                } else if (value instanceof Long) {
                    index = (Long) value;
                } else {
                    throw new IllegalArgumentException(
                            "Unsupported type [" + value.getClass() + "] found for clear index at cache key [" + clearIndexKey + "]");
                }
            }
        }

        return index;
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

	@Override
	public void acquireReadLock(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseReadLock(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void acquireWriteLock(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void releaseWriteLock(String key) {
		// TODO Auto-generated method stub
		
	}
    
}
