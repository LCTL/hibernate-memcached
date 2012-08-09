package com.googlecode.hibernate.memcached.strategy;

import java.util.Comparator;
import java.util.UUID;

import org.hibernate.cache.spi.access.SoftLock;

/**
 * Interface type implemented by all wrapper objects in the cache.
 */
public interface Lockable {

    /**
     * Returns the enclosed value.
     */
    public Object getValue();

    /**
     * Returns <code>true</code> if the enclosed value can be read by a transaction started at the given time.
     */
    public boolean isReadable(long txTimestamp);

    /**
     * Returns <code>true</code> if the given lock can be unlocked using the given SoftLock instance as a handle.
     */
    public boolean isUnlockable(SoftLock lock);

    /**
     * Returns <code>true</code> if the enclosed value can be replaced with one of the given version by a
     * transaction started at the given time.
     */
    public boolean isWriteable(long txTimestamp, Object version, Comparator versionComparator);

    /**
     * Locks this entry, stamping it with the UUID and lockId given, with the lock timeout occuring at the specified
     * time.  The returned Lock object can be used to unlock the entry in the future.
     */
    public Lock lock(long timeout, UUID uuid, long lockId);
}
