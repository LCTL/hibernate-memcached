package com.googlecode.hibernate.memcached

import com.googlecode.hibernate.memcached.client.*;

/**
 * DOCUMENT ME!
 * @author Ray Krueger
 */
class MockMemcached implements HibernateMemcachedClient {

    def cache = [:]

    public Object get(String key) {
        cache[key]
    }

    public boolean set(String key, int cacheTimeSeconds, Object o) {
        cache[key] = o
        return true;
    }

    public boolean add(String key, int exp, Object o) {
        return false;
    }

    public void delete(String key) {
        cache.remove key
    }

    public long incr(String key, long factor, long startingValue) {
        Long counter = (Long) cache[key]
        if (counter != null) {
            cache[key] = counter + 1
        } else {
            cache[key] = counter
        }
        return counter
    }

    public long decr(String key, long by, long startingValue) {
        // implement
    }

    public void shutdown() {

    }


  public Map<String, Object> getMulti(String[] keys) {
    return cache.findAll {key, value -> keys.toList().contains(key)}
  }
}