package com.googlecode.hibernate.memcached.client;

import java.util.Map;

/**
 * Interface to abstract memcache operations.
 *
 * @author Ray Krueger
 */
public interface HibernateMemcachedClient {

    Object get(String key);

    Map<String, Object> getMulti(String... keys);

    /**
     * doc
     * @param key
     * @param cacheTimeSeconds
     * @param o
     * @return
     */
    boolean set(String key, int cacheTimeSeconds, Object o);
    
    boolean add(String key, int exp, Object o);

    void delete(String key);

    long incr(String key, long factor, long startingValue);
    
    long decr(String key, long by, long startingValue);

    void shutdown();
}
