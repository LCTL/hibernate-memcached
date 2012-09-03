package com.googlecode.hibernate.memcached.client;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;

/**
 * An interface used to abstract the creation of 
 * {@link HibernateMemcachedClient}s. 
 * <p>
 * <b>Implementers Note:</b> 
 * <p>
 * When adding a new client it is expected that an implementer of this
 * interface will have a public constructor of the form
 * <p>
 * <code>ClientFactoryImpl({@link com.googlecode.hibernate.memcached.MemcachedProperties} properties)</code>
 *
 * @author Ray Krueger
 */
public interface HibernateMemcachedClientFactory {
    
    /**
     * Creates a new {@link HibernateMemcachedClient} instance
     * 
     * @return a new {@link HibernateMemcachedClient}
     */
    public HibernateMemcachedClient createMemcacheClient();

}
