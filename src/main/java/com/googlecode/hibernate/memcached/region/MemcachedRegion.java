package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.spi.Region;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.concurrent.keylock.ReadWriteKeyLockProvider;

/**
 * 
 *
 */
public interface MemcachedRegion extends Region, HibernateMemcachedClient, ReadWriteKeyLockProvider {
    
    boolean set(String key, Object o);
    
    boolean add(String key, Object o);
    
    boolean clear();

}
