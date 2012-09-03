package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;

/**
 * An abstract implementation of {@link GeneralDataRegion}
 * 
 * @see AbstractMemcachedRegion
 */
public abstract class AbstractGeneralDataRegion extends AbstractMemcachedRegion implements GeneralDataRegion {

    private final Logger log = LoggerFactory.getLogger(AbstractGeneralDataRegion.class);

    /**
     * Creates a new {@link AbstractGeneralDataRegion} using the given client
     * and settings.
     * 
     * @param client   the client used to access Memcached
     * @param settings the settings for this region
     */
    public AbstractGeneralDataRegion(HibernateMemcachedClient client, MemcachedRegionSettings settings) {
        super(client, settings);
    }

    @Override
    public Object get(Object key) throws CacheException {
        return createComponentFactory().createMemcacheClient().get(toKey(key));
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        createComponentFactory().createMemcacheClient().set(toKey(key), getTimeout(), value);
    }

    @Override
    public void evict(Object key) throws CacheException {
        createComponentFactory().createMemcacheClient().delete(toKey(key));
    }

    @Override
    public void evictAll() throws CacheException {
        clear();
    }

}
