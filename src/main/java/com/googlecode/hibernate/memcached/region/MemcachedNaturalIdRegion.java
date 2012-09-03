package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadOnlyMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadWriteMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.TransactionalMemcachedNaturalIdRegionAccessStrategy;

/**
 * Implements the {@link NaturalIdRegion} interface.
 */
public class MemcachedNaturalIdRegion 
    extends AbstractMemcachedTransactionalDataRegion<NaturalIdRegionAccessStrategy>
    implements NaturalIdRegion {

    /**
     * Creates a new {@link MemcachedNaturalIdRegion}.
     * 
     * @param client               the client used to access Memcached
     * @param settings             the settings for this region
     * @param cacheDataDescription the metadata for this region
     */
    public MemcachedNaturalIdRegion(HibernateMemcachedClient client, MemcachedRegionSettings settings, CacheDataDescription cacheDataDescription) {
        super(client, settings, cacheDataDescription);
    }

    @Override
    public NaturalIdRegionAccessStrategy getReadOnlyRegionAccessStrategy() {
        return new ReadOnlyMemcachedNaturalIdRegionAccessStrategy(this);
    }

    @Override
    public NaturalIdRegionAccessStrategy getReadWriteRegionAccessStrategy() {
        return new ReadWriteMemcachedNaturalIdRegionAccessStrategy(this);
    }

    @Override
    public NaturalIdRegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy() {
        return new NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy(this);
    }

    @Override
    public NaturalIdRegionAccessStrategy getTransactionalRegionAccessStrategy() {
        return new TransactionalMemcachedNaturalIdRegionAccessStrategy(this);
    }

}
