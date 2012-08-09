package com.googlecode.hibernate.memcached.strategy;

import java.io.Serializable;
import java.util.Comparator;
import java.util.UUID;

import org.hibernate.cache.spi.access.SoftLock;

/**
 * Wrapper type representing unlocked items.
 */
public final class Item implements Serializable, Lockable {

    private static final long serialVersionUID = 1L;
    private final long timestamp;
    private final Object value;
    private final Object version;

    /**
     * Creates an unlocked item wrapping the given value with a version and creation timestamp.
     */
    Item(Object value, Object version, long timestamp) {
        this.value = value;
        this.version = version;
        this.timestamp = timestamp;
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadable(long txTimestamp) {
        return txTimestamp > timestamp;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUnlockable(SoftLock lock) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
        return version != null && versionComparator.compare(version, newVersion) < 0;
    }

    /**
     * {@inheritDoc}
     */
    public Lock lock(long timeout, UUID uuid, long lockId) {
        return new Lock(timeout, uuid, lockId, version);
    }
}
