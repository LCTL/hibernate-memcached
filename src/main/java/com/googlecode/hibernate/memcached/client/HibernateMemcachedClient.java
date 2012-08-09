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

    void set(String key, int cacheTimeSeconds, Object o);
    
    boolean add(String key, int exp, Object o);

    void delete(String key);

    void incr(String key, int factor, int startingValue);
    
    void decr(String key, int by, int startingValue);

    void shutdown();
}
