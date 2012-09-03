package com.googlecode.hibernate.memcached;

import java.util.Properties;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;
import com.googlecode.hibernate.memcached.client.spymemcached.SpyMemcacheClientFactory;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;
import com.googlecode.hibernate.memcached.strategy.key.ToStringKeyStrategy;
import com.googlecode.hibernate.memcached.strategy.key.encoding.KeyEncodingStrategy;
import com.googlecode.hibernate.memcached.strategy.key.encoding.Sha1KeyEncodingStrategy;
import com.googlecode.hibernate.memcached.utils.PropertiesUtils;
import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * A class for reading the cache-wide properties used when configuring Memcached
 * as a second-level cache for Hibernate. Many of these properties can be
 * configured on a per region basis (see {@link MemcachedRegionProperties}).
 * <p/>
 * <b>Cache wide properties</b>
 * <table border='1'>
 * <tr><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr>
 * <td>hibernate.memcached.memcacheClientFactory</td>
 * <td>{@link com.googlecode.hibernate.memcached.client.spymemcached.SpyMemcacheClientFactory}</td>
 * <td>The {@link HibernateMemcachedClientFactory} used to create {@link HibernateMemcachedClient}s.
 * (e.g. {@link SpyMemcacheClientFactory} or {@link DangaMemcacheClientFactor})</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.readLockPrefix</td>
 * <td>read_lock</td>
 * <td>The key prefix used when generating read lock keys.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.writeLockPrefix</td>
 * <td>write_lock</td>
 * <td>The key prefix used when generating write lock keys.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.clearIndexKeyPrefix</td>
 * <td>index_key</td>
 * <td>The key prefix used when generating clear index keys.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.dogpileTokenKeyPrefix</td>
 * <td>dogpile_token</td>
 * <td>The key prefix used when generating dogpile token keys.</td>
 * </tr>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.nameSpaceSeparator</td>
 * <td>:</td>
 * <td>The separator used when generating keys with multiple components. (eg. [region-name][separator][object-key])</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.cacheTimeSeconds</td>
 * <td>300</td>
 * <td>The number of seconds <code>Object</code>s should remain in the cache.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.clearSupported</td>
 * <td>false</td>
 * <td><code>true</code> if region level clearing is supported, <code>false</code> otherwise.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.dogpilePrevention</td>
 * <td>false</td>
 * <td><code>true</code> if dogpile prevention is supported, <code>false</code> otherwise.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.dogpilePrevention.expirationFactor</td>
 * <td>2</td>
 * <td>The factor by which cache time should be multiplied when dogpile prevention is enabled.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.keyStrategy</td>
 * <td>{@link com.googlecode.hibernate.memcached.strategy.key.ToStringKeyStrategy}</td>
 * <td>The {@link KeyStrategy} used to turn a key <code>Object</code> into a key <code>String</code>.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.keyEncodingStrategy</td>
 * <td>{@link com.googlecode.hibernate.memcached.strategy.key.encoding.Sha1KeyEncodingStrategy}</td>
 * <td>The {@link KeyEncodingStrategy} used to encode the full key <code>String</code>.</td>
 * </tr>
 * </table>
 * 
 * @see PropertiesUtils
 * @see MemcachedRegionProperties
 */
public class MemcachedProperties extends Properties {

    private static final long serialVersionUID = 1L;

    protected static final String PROP_PREFIX = "hibernate.memcached.";
    
    // Keys
    protected static final String MEMCACHE_CLIENT_FACTORY = "memcacheClientFactory";
    
    protected static final String READ_LOCK_KEY_PREFIX = "readLockPrefix";
    protected static final String WRITE_LOCK_KEY_PREFIX = "writeLockPrefix";
    protected static final String CLEAR_INDEX_KEY_PREFIX = "clearIndexKeyPrefix";
    protected static final String DOGPILE_TOKEN_KEY_PREFIX = "dogpileTokenKeyPrefix";
    protected static final String NAME_SPACE_SEPARATOR = "nameSpaceSeparator";

    protected static final String CACHE_TIME_SECONDS = "cacheTimeSeconds";
    protected static final String CLEAR_SUPPORTED = "clearSupported";
    protected static final String DOGPILE_PREVENTION = "dogpilePrevention";
    protected static final String DOGPILE_PREVENTION_EXPIRATION_FACTOR = "dogpilePrevention.expirationFactor";
    protected static final String KEY_STRATEGY = "keyStrategy";
    protected static final String KEY_ENCODING_STRATEGY = "keyEncodingStrategy";
    
    // Defaults
    protected static final String DEFAULT_MEMCACHE_CLIENT_FACTORY_NAME = SpyMemcacheClientFactory.class.getName();

    protected static final String DEFAULT_READ_LOCK_KEY_PREFIX = "read_lock";
    protected static final String DEFAULT_WRITE_LOCK_KEY_PREFIX = "write_lock";
    protected static final String DEFAULT_CLEAR_INDEX_KEY_PREFIX = "index_key";
    protected static final String DEFAULT_DOGPILE_TOKEN_KEY_PREFIX = "dogpile_token";
    protected static final String DEFAULT_NAME_SPACE_SEPARATOR = ":";
    
    protected static final int DEFAULT_CACHE_TIME_SECONDS = 300;
    protected static final boolean DEFAULT_CLEAR_SUPPORTED = false;
    protected static final boolean DEFAULT_DOGPILE_PREVENTION = false;
    protected static final KeyStrategy DEFAULT_KEY_STRATEGY = new ToStringKeyStrategy();
    protected static final KeyEncodingStrategy DEFAULT_KEY_ENCODING_STRATEGY = new Sha1KeyEncodingStrategy();
    protected static final int DEFAULT_DOGPILE_EXPIRATION_FACTOR = 2;
    
    /**
     * Creates a new {@link MemcachedProperties} using the passed in 
     * {@link Properties}.
     * 
     * @param properties the initial properties
     */
    public MemcachedProperties(Properties properties) {
        super(properties);
    }

    // Property Access Methods
    
    /**
     * Gets the configured {@link HibernateMemcachedClientFactory} specified by 
     * the given properties.
     * 
     * @return a new client factory
     */
    public HibernateMemcachedClientFactory getMemcachedClientFactory() {
        String key = toKey(MEMCACHE_CLIENT_FACTORY);
        String clientFactoryName = get(key, DEFAULT_MEMCACHE_CLIENT_FACTORY_NAME);
        return StringUtils.newInstance(clientFactoryName, this);
    }
    
    /**
     * Gets the configured read lock key prefix <code>String</code>.
     * 
     * @return the configured read lock key prefix <code>String</code>
     */
    public String getReadLockKeyPrefix() {
        String key = toKey(READ_LOCK_KEY_PREFIX);
        return get(key, DEFAULT_READ_LOCK_KEY_PREFIX);
    }
    
    /**
     * Gets the configured write lock key prefix <code>String</code>.
     * 
     * @return the configured write lock key prefix <code>String</code>
     */
    public String getWriteLockKeyPrefix() {
        String key = toKey(WRITE_LOCK_KEY_PREFIX);
        return get(key, DEFAULT_WRITE_LOCK_KEY_PREFIX);
    }
    
    /**
     * Gets the configured clear index key prefix <code>String</code>.
     * 
     * @return the configured clear index key prefix <code>String</code>
     */
    public String getClearIndexKeyPrefix() {
        String key = toKey(CLEAR_INDEX_KEY_PREFIX);
        return get(key, DEFAULT_CLEAR_INDEX_KEY_PREFIX);
    }
    
    /**
     * Gets the configured dogpile token key prefix <code>String</code>.
     * 
     * @return the configured dogpile token key prefix <code>String</code>
     */
    public String getDogpileTokenKeyPrefix() {
        String key = toKey(DOGPILE_TOKEN_KEY_PREFIX);
        return get(key, DEFAULT_DOGPILE_TOKEN_KEY_PREFIX);
    }
    
    /**
     * Gets the configured namespace separator <code>String</code>.
     * 
     * @return the configured namespace separator <code>String</code>
     */
     public String getNamespaceSeparator() {
         String key = toKey(NAME_SPACE_SEPARATOR);
        return get(key, DEFAULT_NAME_SPACE_SEPARATOR);
     }
    
     /**
      * Gets the configured cache time, in seconds.
      * 
      * @return the configured cache time, in seconds
      */
    public int getCacheTimeSeconds() {
        String key = toKey(CACHE_TIME_SECONDS);
        return getInt(key, DEFAULT_CACHE_TIME_SECONDS);
    }

    /**
     * Gets the configured key strategy.
     * 
     * @return the configured key strategy
     */
    public KeyStrategy getKeyStrategy() {
        String key = toKey(KEY_STRATEGY);
        return getObject(key, DEFAULT_KEY_STRATEGY);
    }
    
    /**
     * Gets the configured key encoding strategy.
     * 
     * @return the configured key encoding strategy
     */
    public KeyEncodingStrategy getKeyEncodingStrategy() {
        String key = toKey(KEY_ENCODING_STRATEGY);
        return getObject(key, DEFAULT_KEY_ENCODING_STRATEGY);
    }

    /**
     * Determines if region level clearing is enabled.
     * 
     * @return <code>true</code> if region level clearing is enabled,
     *         <code>false</code> otherwise
     */
    public boolean isClearSupported() {
        String key = toKey(CLEAR_SUPPORTED);
        return getBoolean(key, DEFAULT_CLEAR_SUPPORTED);
    }

    /**
     * Determines if dogpile prevention is enabled.
     * 
     * @return <code>true</code> if dogpile prevention is enabled,
     *         <code>false</code> otherwise
     */
    public boolean isDogpilePreventionEnabled() {
        String key = toKey(DOGPILE_PREVENTION);
        return getBoolean(key, DEFAULT_DOGPILE_PREVENTION);
    }

    /**
     * Gets the configured dogpile prevention expiration factor.
     * 
     * @return the configured expiration factor
     */
    public int getDogpilePreventionExpirationFactor() {
        String key = toKey(DOGPILE_PREVENTION_EXPIRATION_FACTOR);
        return getInt(key, DEFAULT_DOGPILE_EXPIRATION_FACTOR);
    }
    
    // Helper Methods
    
    public String get(String key) {
        return this.getProperty(key);
    }

    public String get(String key, String defaultVal) {
        return PropertiesUtils.get(this, key, defaultVal);
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
    
    /**
     * Gets the full key for the given property name.
     * 
     * @param propertyName the name of the property to get the full key for
     * @return             the full key for the given property
     */
    protected String toKey(String propertyName) {
        return PROP_PREFIX + propertyName;
    }
}
