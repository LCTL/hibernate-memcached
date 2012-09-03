package com.googlecode.hibernate.memcached.concurrent.keylock;

/**
 * An interface for adding Read/Write locking to Memcached on a per key basis.
 * <p>
 * <b>Usage Note:</b> Currently it is required that lock releasing only be done
 * after a successful acquire.
 * <p>
 * (Would it be better to create a lock object that is acquired and released?)
 */
public interface MemcachedReadWriteKeyLockProvider {

    /**
     * Acquires a read lock on the given key.
     * 
     * @param key the key to acquire a read lock on
     * @return    <code>true</code> if the lock was acquired, 
     *            <code>false</code> otherwise
     */
    public boolean acquireReadLock(String key);

    /**
     * Releases a read lock on the given key.
     * 
     * @param key the key to release a read lock on
     * @return    <code>true</code> if the lock was released,
     *            <code>false</code> otherwise
     */
    public boolean releaseReadLock(String key);

    /**
     * Acquire a write lock on the given key.
     * 
     * @param key the key to acquire a write lock on
     * @return    <code>true</code> if the lock was acquired,
     *            <code>false</code> otherwise
     */
    public boolean acquireWriteLock(String key);

    /**
     * Releases a write lock on the given key.
     * 
     * @param key the key to release a write lock on
     * @return    <code>true</code> if the write lock was released,
     *            <code>false</code> otherwise
     */
    public boolean releaseWriteLock(String key);

}
