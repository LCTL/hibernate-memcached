package com.googlecode.hibernate.memcached.client;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;

/**
 * Simple interface used to abstract the creation of the MemcachedClient
 * All implementers must have a constructor that takes an instance of
 * {@link com.googlecode.hibernate.memcached.MemcachedProperties}.
 *
 * @author Ray Krueger
 */
public interface HibernateMemcachedClientFactory {

    HibernateMemcachedClient createMemcacheClient() throws Exception;

}
