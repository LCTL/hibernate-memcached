package com.googlecode.hibernate.memcached.region;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;

public abstract class AbstractGeneralDataRegion extends AbstractMemcachedRegion implements GeneralDataRegion {

    private final Logger log = LoggerFactory.getLogger(AbstractGeneralDataRegion.class);

    public AbstractGeneralDataRegion(MemcachedCache cache, Properties properties, HibernateMemcachedClient client) {
        super(cache);
    }

    public Object get(Object key) throws CacheException {
        return cache.get(key);
    }

    public void put(Object key, Object value) throws CacheException {
        cache.put(key, value);
    }

    public void evict(Object key) throws CacheException {
        cache.remove(key);
    }

    public void evictAll() throws CacheException {
        cache.clear();
    }

}
