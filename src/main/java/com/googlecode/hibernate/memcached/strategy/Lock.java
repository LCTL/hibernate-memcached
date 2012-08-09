package com.googlecode.hibernate.memcached.strategy;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

import org.hibernate.cache.spi.access.SoftLock;

/**
 * Wrapper type representing locked items.
 */
public final class Lock implements Serializable, Lockable, SoftLock {

    private static final long serialVersionUID = 2L;

    private boolean concurrent;
    private final long lockId;
    private int multiplicity = 1;

    private final UUID sourceUuid;
    private long timeout;
    private long unlockTimestamp;
    private final Object version;

    /**
     * Creates a locked item with the given identifiers and object version.
     */
    public Lock(long timeout, UUID sourceUuid, long lockId, Object version) {
        this.timeout = timeout;
        this.lockId = lockId;
        this.version = version;
        this.sourceUuid = sourceUuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Lock) {
            return (lockId == ((Lock) o).lockId) && sourceUuid.equals(((Lock) o).sourceUuid);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = (sourceUuid != null ? sourceUuid.hashCode() : 0);
        int temp = (int) lockId;
        for (int i = 1; i < Long.SIZE / Integer.SIZE; i++) {
            temp ^= (lockId >>> (i * Integer.SIZE));
        }
        return hash + temp;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadable(long txTimestamp) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUnlockable(SoftLock lock) {
        return equals(lock);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
        if (txTimestamp > timeout) {
            // if timedout then allow write
            return true;
        }
        if (multiplicity > 0) {
            // if still locked then disallow write
            return false;
        }
        return version == null ? txTimestamp > unlockTimestamp : versionComparator.compare(version, newVersion) < 0;
    }

    /**
     * {@inheritDoc}
     */
    public Lock lock(long timeout, UUID uuid, long lockId) {
        concurrent = true;
        multiplicity++;
        this.timeout = timeout;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Lock Source-UUID:" + sourceUuid + " Lock-ID:" + lockId);
        return sb.toString();
    }

    /**
     * Unlocks this Lock, and timestamps the unlock event.
     */
    public void unlock(long timestamp) {
        if (--multiplicity == 0) {
            unlockTimestamp = timestamp;
        }
    }

    /**
     * Returns true if this Lock has been concurrently locked by more than one transaction.
     */
    public boolean wasLockedConcurrently() {
        return concurrent;
    }
}
