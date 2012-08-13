package com.googlecode.hibernate.memcached;

import java.util.Properties;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;
import com.googlecode.hibernate.memcached.client.spymemcached.SpyMemcacheClientFactory;
import com.googlecode.hibernate.memcached.utils.PropertiesUtils;
import com.googlecode.hibernate.memcached.utils.StringUtils;

public class MemcachedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    protected static final String PROP_PREFIX = "hibernate.memcached.";
    
    // Keys
    protected static final String MEMCACHE_CLIENT_FACTORY = "memcacheClientFactory";
    
    // Defaults
    protected static final String DEFAULT_MEMCACHE_CLIENT_FACTORY_NAME = SpyMemcacheClientFactory.class.getName();

    public MemcachedProperties(Properties properties) {
        super(properties);
    }

    // Property Accessor Methods
    
    public HibernateMemcachedClientFactory getMemcachedClientFactory() {
        String clientFactoryKey = makeKey(MEMCACHE_CLIENT_FACTORY);
        String clientFactoryName = get(clientFactoryKey, DEFAULT_MEMCACHE_CLIENT_FACTORY_NAME);
        return StringUtils.newInstance(clientFactoryName, this);
    }
    
    // Helper Methods
    
    public String get(String key) {
        return this.getProperty(key);
    }

    public String get(String key, String defaultVal) {
        return PropertiesUtils.get(this, key, defaultVal);
    }

    public String findValue(String... keys) {
        return PropertiesUtils.findValue(this, keys);
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return PropertiesUtils.getBoolean(this, key, defaultVal);
    }

    public long getLong(String key, long defaultVal) {
        return PropertiesUtils.getLong(this, key, defaultVal);
    }

    public int getInt(String key, int defaultVal) {
        return PropertiesUtils.getInt(this, key, defaultVal);
    }

    public double getDouble(String key, double defaultVal) {
        return PropertiesUtils.getDouble(this, key, defaultVal);
    }

    public <T extends Enum<T>> T getEnum(String key, Class<T> type, T defaultValue) {
        return PropertiesUtils.getEnum(this, key, type, defaultValue);
    }
    
    public <T extends Object> T getObject(String key, T defaultValue, Object ... args) {
        return PropertiesUtils.getObject(this, key, defaultValue, args);
    }
    
    protected String makeKey(String keyName) {
        return PROP_PREFIX + keyName;
    }
}
