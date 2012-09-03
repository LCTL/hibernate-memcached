package com.googlecode.hibernate.memcached;

import java.util.Properties;

import com.googlecode.hibernate.memcached.region.MemcachedRegion;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;
import com.googlecode.hibernate.memcached.strategy.key.encoding.KeyEncodingStrategy;

/**
 * A class for reading region-wide properties used when configuring Memcached 
 * as a second-level cache for Hibernate. The default values are defined by
 * {@link MemcachedProperties}.
 * <p/>
 * <b>Cache Region properties</b><br/>
 * Cache region properties are set by giving your cached data a "region name" 
 * in hibernate. You can tune the {@link MemcachedRegion} using the following
 * properties.
 * <p/>
 * <table border='1'>
 * <tr><th>Property</th><th>Description</th></tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].readLockPrefix</td>
 * <td>The key prefix used when generating read lock keys.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].writeLockPrefix</td>
 * <td>The key prefix used when generating write lock keys.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].clearIndexKeyPrefix</td>
 * <td>The key prefix used when generating clear index keys.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].dogpileTokenKeyPrefix</td>
 * <td>The key prefix used when generating dogpile token keys.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].nameSpaceSeparator</td>
 * <td>The separator used when generating keys with multiple components. (eg. [region-name][separator][object-key])</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].cacheTimeSeconds</td>
 * <td>The number of seconds <code>Object</code>s should remain in the cache.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].clearSupported</td>
 * <td><code>true</code> if region level clearing is supported, <code>false</code> otherwise.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].dogpilePrevention</td>
 * <td><code>true</code> if dogpile prevention is supported, <code>false</code> otherwise.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].dogpilePrevention.expirationFactor</td>
 * <td>The factor by which cache time should be multiplied when dogpile prevention is enabled.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].keyStrategy</td>
 * <td>The {@link KeyStrategy} used to turn a key <code>Object</code> into a key <code>String</code>.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.[region-name].keyEncodingStrategy</td>
 * <td>The {@link KeyEncodingStrategy} used to encode the full key <code>String</code>.</td>
 * </tr>
 * </table>
 *
 * @see MemcachedProperties
 */
public class MemcachedRegionProperties extends MemcachedProperties {

    private static final long serialVersionUID = 1L;
 
    /**
     * Creates a new {@link MemcachedRegionProperties} backed by the given 
     * {@link Properties}.
     * 
     * @param properties the properties to read from
     */
    public MemcachedRegionProperties(Properties properties) {
        super(properties);
    }
    
    // Property Accessor Methods
    
    /**
     * See {@link MemcachedProperties#getReadLockKeyPrefix()}.
     * 
     * @param cacheRegion the name of the region to get the prefix for
     */
    public String getReadLockKeyPrefix(String cacheRegion) {
        String key = toKey(READ_LOCK_KEY_PREFIX, cacheRegion);
        String result = get(key, null);
        return result != null ? result : getReadLockKeyPrefix();
    }
    
    /**
     * See {@link MemcachedProperties#getWriteLockKeyPrefix()}.
     * 
     * @param cacheRegion the name of the region to get the prefix for
     */
    public String getWriteLockKeyPrefix(String cacheRegion) {
        String key = toKey(WRITE_LOCK_KEY_PREFIX, cacheRegion);
        String result = get(key, null);
        return result != null ? result : getWriteLockKeyPrefix();
    }
    
    /**
     * See {@link MemcachedProperties#getClearIndexKeyPrefix()}.
     * 
     * @param cacheRegion the name of the region to get the prefix for
     */
    public String getClearIndexKeyPrefix(String cacheRegion) {
        String key = toKey(CLEAR_INDEX_KEY_PREFIX, cacheRegion);
        String result = get(key, null);
        return result != null ? result : getClearIndexKeyPrefix();
    }
    
    /**
     * See {@link MemcachedProperties#getDogpileTokenKeyPrefix()}.
     * 
     * @param cacheRegion the name of the region to get the prefix for
     */
    public String getDogpileTokenKeyPrefix(String cacheRegion) {
        String key = toKey(DOGPILE_TOKEN_KEY_PREFIX, cacheRegion);
        String result = get(key, null);
        return result != null ? result : getDogpileTokenKeyPrefix();
    }
    
     /**
      * See {@link MemcachedProperties#getNamespaceSeparator()}.
      * 
      * @param cacheRegion the name of the region to get the separator for
      */
     public String getNamespaceSeparator(String cacheRegion) {
         String key = toKey(NAME_SPACE_SEPARATOR, cacheRegion);
         String result = get(key, null);
        return result != null ? result : getNamespaceSeparator();
     }
    
    /**
     * See {@link MemcachedProperties#getCacheTimeSeconds()}.
     * 
     * @param cacheRegion the name of the region to get the cache time for
     */
    public int getCacheTimeSeconds(String cacheRegion) {
        String key = toKey(CACHE_TIME_SECONDS, cacheRegion);
        int result = getInt(key, getCacheTimeSeconds());
        return result;
    }
    
    /**
     * See {@link MemcachedProperties#getKeyStrategy()}.
     * 
     * @param cacheRegion the name of the region to get the key strategy for
     */
    public KeyStrategy getKeyStrategy(String cacheRegion) {
        String key = toKey(KEY_STRATEGY, cacheRegion);
        KeyStrategy result = getObject(key, null);
        return result != null ? result : getKeyStrategy();
    }
    
    /**
     * See {@link MemcachedProperties#getKeyEncodingStrategy()}.
     * 
     * @param cacheRegion the name of the region to get the encoding strategy
     *                    for
     */
    public KeyEncodingStrategy getKeyEncodingStrategy(String cacheRegion) {
        String key = toKey(KEY_ENCODING_STRATEGY, cacheRegion);
        KeyEncodingStrategy result = getObject(key, null);
        return result != null ? result : getKeyEncodingStrategy();
    }

    /**
     * See {@link MemcachedProperties#isClearSupported()}.
     * 
     * @param cacheRegion the name of the region to get the clearing status for
     */
    public boolean isClearSupported(String cacheRegion) {
        String key = toKey(CLEAR_SUPPORTED, cacheRegion);
        boolean result = getBoolean(key, isClearSupported());
        return result;
    }

    /**
     * See {@link MemcachedProperties#isDogpilePreventionEnabled()}.
     * 
     * @param cacheRegion the name of the region to get the dogpile status for
     */
    public boolean isDogpilePreventionEnabled(String cacheRegion) {
        String key = toKey(DOGPILE_PREVENTION, cacheRegion);
        boolean result = getBoolean(key, isDogpilePreventionEnabled());
        return result;
    }

    /**
     * See {@link MemcachedProperties#getDogpilePreventionExpirationFactor()}.
     * 
     * @param cacheRegion the name of the region to get the expiration factor
     *                    for
     */
    public int getDogpilePreventionExpirationFactor(String cacheRegion) {
        String key = toKey(DOGPILE_PREVENTION_EXPIRATION_FACTOR, cacheRegion);
        int result = getInt(key, getDogpilePreventionExpirationFactor());
        return result;
    }

    // Helper Methods
    
    private String get(String key, String region, String defaultVal) {
        String globalVal = get(toKey(key), defaultVal);
        return get(toKey(key, region), globalVal);
    }

    private boolean getBoolean(String key, String region, boolean defaultVal) {
        boolean globalVal = getBoolean(toKey(key), defaultVal);
        return getBoolean(toKey(key, region), globalVal);
    }

    private int getInt(String key, String region, int defaultVal) {
        int globalValue = getInt(toKey(key), defaultVal);
        return getInt(toKey(key, region), globalValue);
    }
    
    private <T extends Object> T getObject(String key, String region, T defaultValue, Object ... args) {
        T globalValue = getObject(toKey(key), defaultValue, args);
        return getObject(toKey(key, region), globalValue, args);
    }
    
    /**
     * See {@link MemcachedProperties#toKey(String)}.
     * 
     * @param propertyName the name of the property to get the full key for
     * @param region       the name of the region to get the full key for
     */
    protected String toKey(String propertyName, String region) {
        return PROP_PREFIX + region + "." + propertyName;
    }
}
