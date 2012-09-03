package com.googlecode.hibernate.memcached.strategy.clear;

import org.hibernate.cache.CacheException;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.key.encoding.KeyEncodingStrategy;
import com.googlecode.hibernate.memcached.utils.MemcachedRegionSettingsUtils;

/**
 * A class that adds region level clearing to Memcached. Clients should use the
 * current value of {@link MemcachedRegionClearStrategy#getClearIndex()} as a
 * part of their key generation strategy.
 * <p>
 * This implementation uses Memcached to store clear indices.
 * 
 * @see ClearStrategy
 */
public class MemcachedRegionClearStrategy implements ClearStrategy {

    private HibernateMemcachedClient client;
    private MemcachedRegionSettings settings;
    
    private final String encodedClearIndexKey;
    
    /**
     * Creates a new {@link MemcachedRegionClearStrategy}.
     * 
     * @param client   the client used to store clear indices
     * @param settings the settings for this {@link ClearStrategy}s region
     */
    public MemcachedRegionClearStrategy(HibernateMemcachedClient client, MemcachedRegionSettings settings) {
        this.client = client;
        this.settings = settings;
        
        KeyEncodingStrategy keyEncodingStrategy =
                MemcachedRegionSettingsUtils.getValidatedMemcachedKeyEncodingStrategy(settings);
        this.encodedClearIndexKey = keyEncodingStrategy.encode(
                MemcachedRegionSettingsUtils.getFullClearIndexKeyPrefix(settings));
    }
    
    /**
     * {@inheritDoc}<br>
     * If clear functionality is disabled for this region it will always
     * return <code>false</code>.
     *
     * @throws CacheException if something went wrong in the cache
     * @see ClearStrategy
     */
    @Override
    public boolean clear() throws CacheException {
        if (settings.isClearSupported()) {
            return client.incr(encodedClearIndexKey, 1, 1) != -1;
        }
        return false;
    }

    /**
     * {@inheritDoc}<br>
     * If clear functionality is disabled for this region it will always
     * return <code>0</code>.
     *
     * @throws CacheException if something went wrong in the cache
     * @see ClearStrategy
     */
    @Override
    public long getClearIndex() throws CacheException {
        if (settings.isClearSupported()) {
            Object value = client.get(encodedClearIndexKey);
            
            if (value != null) {
                if (value instanceof String) {
                    return Long.valueOf((String) value);
                } else if (value instanceof Long) {
                    return (Long) value;
                } else {
                    throw new IllegalArgumentException(String.format(
                        "Unsupported type [%s] found for clear index at cache key [%s]",
                        value.getClass(), encodedClearIndexKey));
                }
            }
        }

        return 0;
    }

}
