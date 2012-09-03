package com.googlecode.hibernate.memcached.client;

import java.util.Map;

/**
 * An interface to abstract
 * <a href="https://github.com/memcached/memcached/blob/master/doc/protocol.txt">
 * memcached operations</href>.
 *
 * @author  Ray Krueger
 */
public interface HibernateMemcachedClient {

    /**
     * Gets an <code>Object</code> from the cache.
     * 
     * @param key the identifier to look up
     * @return    an {@link Object} from the cache or <code>null</code>
     */
    public Object get(String key);

    /**
     * Gets multiple <code>Object</code>s from the cache in a single operation.
     * 
     * @param keys an array of identifiers to look up
     * @return     a {@link Map} of each key to its associated {@link Object} 
     *             from the cache, if one exists. Or an empty {@link Map}.
     */
    public Map<String, Object> getMulti(String... keys);

    /**
     * Stores an <code>Object</code> in the cache under the given key for some
     * number of seconds. 
     * 
     * (change interface so the object must implement {@link java.io.Serializable}?)
     * 
     * @param key              an identifier to store the {@link Object} under
     * @param cacheTimeSeconds the number of seconds to store the given {@link Object}
     * @param o                the {@link Object} to store
     * @return                 <code>true</code> if the object was successfully
     *                         stored, <code>false</code> otherwise
     */
    public boolean set(String key, int cacheTimeSeconds, Object o);
    
    /**
     * Stores an <code>Object</code> in the cache under the given key for
     * some number of seconds, but only if no <code>Object</code> is currently
     * stored under the given key.
     * 
     * @param key an identifier to store the {@link Object} under
     * @param exp the number of seconds to store the given {@link Object}
     * @param o   the {@link Object} to store
     * @return    <code>true</code> if the object was successfully stored,
     *            <code>false</code> otherwise
     */
    public boolean add(String key, int exp, Object o);

    /**
     * Removes an <code>Object</code> from the cache.
     * 
     * @param key the identifier for the {@link Object} to remove
     * @return <code>true</code> if the object was successfully removed,
     *         <code>false</code> otherwise
     */
    public boolean delete(String key);

    /**
     * Atomicly increments the numeric value stored under the given key.
     * 
     * @param key           an identifier for the stored number
     * @param factor        the amount to increment by
     * @param startingValue a starting value for the key, used if no value
     *                      is stored under the given key
     * @return              the new value, or <code>-1</code>
     */
    long incr(String key, long factor, long startingValue);
    
    /**
     * Atomicaly decrements the numeric value stored under the given key.
     * 
     * @param key           an identifier for the stored number
     * @param by            the amount to decrement by
     * @param startingValue a starting value for the key, used if no value
     *                      is stored under the given key
     * @return              the new value, or <code>-1</code>
     */
    long decr(String key, long by, long startingValue);

    /**
     * Initiates client shutdown.
     */
    void shutdown();
}
