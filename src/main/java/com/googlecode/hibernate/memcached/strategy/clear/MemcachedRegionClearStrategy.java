package com.googlecode.hibernate.memcached.strategy.clear;

import org.hibernate.cache.CacheException;

import com.googlecode.hibernate.memcached.MemcachedRegionPropertiesHolder;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;

public class MemcachedRegionClearStrategy implements ClearStrategy {

    private HibernateMemcachedClient client;
    private MemcachedRegionPropertiesHolder regionProperties;
    private final String clearIndexKey;
    
    public MemcachedRegionClearStrategy(HibernateMemcachedClient client, 
            MemcachedRegionPropertiesHolder regionProperties) {
        this.client = client;
        this.regionProperties = regionProperties;
        
        this.clearIndexKey = new StringBuilder()
        .append(regionProperties.getClearIndexKeyPrefix())
        .append(regionProperties.getNamespaceSeparator())
        .append(regionProperties.getName())
        .toString();
    }
    
    /**
     * Clear functionality is disabled by default.
     * Read this class's javadoc for more detail.
     *
     * @throws CacheException
     * @see com.googlecode.hibernate.memcached.MemcachedCache
     */
    @Override
    public boolean clear() throws CacheException {
        if (regionProperties.isClearSupported()) {
            client.incr(clearIndexKey, 1, 1);
            return true;
        }
        return false;
    }

	@Override
    public long getClearIndex() {
        long index = 0;

        if (regionProperties.isClearSupported()) {
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

}
