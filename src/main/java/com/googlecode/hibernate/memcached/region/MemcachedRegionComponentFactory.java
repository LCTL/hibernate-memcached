package com.googlecode.hibernate.memcached.region;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;
import com.googlecode.hibernate.memcached.concurrent.keylock.MemcachedReadWriteKeyLockProviderFactory;

/**
 * An interface to abstract the creation of a {@link MemcachedRegion}s underling
 * components which may need to share some state.
 */
public interface MemcachedRegionComponentFactory extends HibernateMemcachedClientFactory,
    MemcachedReadWriteKeyLockProviderFactory {
}
