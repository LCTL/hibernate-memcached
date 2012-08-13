package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionPropertiesHolder;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;

public abstract class AbstractGeneralDataRegion extends AbstractMemcachedRegion implements GeneralDataRegion {

    private final Logger log = LoggerFactory.getLogger(AbstractGeneralDataRegion.class);

    public AbstractGeneralDataRegion(HibernateMemcachedClient client, MemcachedRegionPropertiesHolder properties, Settings settings) {
        super(client, properties, settings);
    }

    public Object get(Object key) throws CacheException {
        return get(String.valueOf(key));
    }

    public void put(Object key, Object value) throws CacheException {
        set(String.valueOf(key), value);
    }

    public void evict(Object key) throws CacheException {
        delete(String.valueOf(key));
    }

    public void evictAll() throws CacheException {
        clear();
    }

}
