package com.googlecode.hibernate.memcached.concurrent.keylock;

/**
 * An interface for classes that create 
 * {@link MemcachedReadWriteKeyLockProvider}s.
 */
public interface MemcachedReadWriteKeyLockProviderFactory {

    /**
     * Creates a new {@link MemcachedReadWriteKeyLockProvider}.
     * 
     * @return a new {@link MemcachedReadWriteKeyLockProvider}
     */
    public MemcachedReadWriteKeyLockProvider createMemcachedReadWriteKeyLockProvider();
    
}
