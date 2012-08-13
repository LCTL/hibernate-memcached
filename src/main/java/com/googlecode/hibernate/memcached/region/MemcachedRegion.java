package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.spi.Region;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;

/**
 * 
 *
 */
public interface MemcachedRegion extends Region, HibernateMemcachedClient {
    
    boolean set(String key, Object o);
    
    boolean add(String key, Object o);
    
    void clear();
    
    void acquireReadLock(String key);
    
    void releaseReadLock(String key);
    
    void acquireWriteLock(String key);
    
    void releaseWriteLock(String key);
    
    
}
