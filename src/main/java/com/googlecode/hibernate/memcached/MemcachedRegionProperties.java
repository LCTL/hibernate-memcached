package com.googlecode.hibernate.memcached;

import java.util.Properties;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;
import com.googlecode.hibernate.memcached.strategy.key.Sha1KeyStrategy;
import com.googlecode.hibernate.memcached.utils.StringUtils;

public class MemcachedRegionProperties extends MemcachedProperties {

    private static final long serialVersionUID = 1L;
    
    // Keys
    protected static final String READ_LOCK_KEY_PREFIX = "readLockPrefix";
    protected static final String WRITE_LOCK_KEY_PREFIX = "writeLockPrefix";
    protected static final String CLEAR_INDEX_KEY_PREFIX = "clearIndexKeyPrefix";
    protected static final String NAME_SPACE_SEPARATOR = "nameSpaceSeparator";

    protected static final String CACHE_TIME_SECONDS = "cacheTimeSeconds";
    protected static final String CLEAR_SUPPORTED = "clearSupported";
    protected static final String DOGPILE_PREVENTION = "dogpilePrevention";
    protected static final String DOGPILE_PREVENTION_EXPIRATION_FACTOR = "dogpilePrevention.expirationFactor";
    protected static final String KEY_STRATEGY = "keyStrategy";
    
    // Defaults
    protected static final String DEFAULT_READ_LOCK_KEY_PREFIX = "read_lock";
    protected static final String DEFAULT_WRITE_LOCK_KEY_PREFIX = "write_lock";
    protected static final String DEFAULT_CLEAR_INDEX_KEY_PREFIX = "index_key";
    protected static final String DEFAULT_NAME_SPACE_SEPARATOR = ":";
    
    protected static final int DEFAULT_CACHE_TIME_SECONDS = 300;
    protected static final boolean DEFAULT_CLEAR_SUPPORTED = false;
    protected static final boolean DEFAULT_DOGPILE_PREVENTION = false;
    protected static final KeyStrategy DEFAULT_KEY_STRATEGY = new Sha1KeyStrategy();
    protected static final int DEFAULT_DOGPILE_EXPIRATION_FACTOR = 2;
	
    public MemcachedRegionProperties(Properties properties) {
        super(properties);
    }
    
    // Property Accessor Methods
    
    public String getReadLockKeyPrefix(String cacheRegion) {
        return get(READ_LOCK_KEY_PREFIX, cacheRegion, DEFAULT_READ_LOCK_KEY_PREFIX);
    }
    
    public String getWriteLockKeyPrefix(String cacheRegion) {
        return get(WRITE_LOCK_KEY_PREFIX, cacheRegion, DEFAULT_WRITE_LOCK_KEY_PREFIX);
    }
    public String getClearIndexKeyPrefix(String cacheRegion) {
        return get(CLEAR_INDEX_KEY_PREFIX, cacheRegion, DEFAULT_CLEAR_INDEX_KEY_PREFIX);
    }
    
     public String getNamespaceSeparator(String cacheRegion) {
        return get(NAME_SPACE_SEPARATOR, cacheRegion, DEFAULT_NAME_SPACE_SEPARATOR);
     }
    
    public int getCacheTimeSeconds(String cacheRegion) {
        return getInt(CACHE_TIME_SECONDS, cacheRegion, DEFAULT_CACHE_TIME_SECONDS);
    }

    public KeyStrategy getKeyStrategy(String cacheRegion) {
        return this.getObject(KEY_STRATEGY, cacheRegion, DEFAULT_KEY_STRATEGY);
    }

    public boolean isClearSupported(String cacheRegion) {
        return getBoolean(CLEAR_SUPPORTED, cacheRegion, DEFAULT_CLEAR_SUPPORTED);
    }

    public boolean isDogpilePreventionEnabled(String cacheRegion) {
        return getBoolean(DOGPILE_PREVENTION, cacheRegion, DEFAULT_DOGPILE_PREVENTION);
    }

    public int getDogpilePreventionExpirationFactor(String cacheRegion) {
        return getInt(DOGPILE_PREVENTION_EXPIRATION_FACTOR, cacheRegion, DEFAULT_DOGPILE_EXPIRATION_FACTOR);
    }

    // Helper Methods
    
    private String get(String key, String region, String defaultVal) {
        String globalVal = get(makeKey(key), defaultVal);
        return get(makeKey(key, region), globalVal);
    }

    private boolean getBoolean(String key, String region, boolean defaultVal) {
        boolean globalVal = getBoolean(makeKey(key), defaultVal);
        return getBoolean(makeKey(key, region), globalVal);
    }

    private int getInt(String key, String region, int defaultVal) {
        int globalValue = getInt(makeKey(key), defaultVal);
        return getInt(makeKey(key, region), globalValue);
    }
    
    private <T extends Object> T getObject(String key, String region, T defaultValue, Object ... args) {
        T globalValue = getObject(makeKey(key), defaultValue, args);
        return getObject(makeKey(key, region), globalValue, args);
    }
    
    protected String makeKey(String keyName, String region) {
        return PROP_PREFIX + region + "." + keyName;
    }
}
