package com.googlecode.hibernate.memcached.region;

import java.util.Properties;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadOnlyMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadWriteMemcachedNaturalIdRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.TransactionalMemcachedNaturalIdRegionAccessStrategy;

public class MemcachedNaturalIdRegion 
    extends AbstractMemcachedTransactionalDataRegion<NaturalIdRegionAccessStrategy>
    implements NaturalIdRegion {

    public MemcachedNaturalIdRegion(MemcachedCache cache, Settings settings, 
            CacheDataDescription metadata, Properties properties, HibernateMemcachedClient client) {
        super(cache, settings, metadata);
    }

    public NaturalIdRegionAccessStrategy getReadOnlyRegionAccessStrategy(Settings settings) {
        return new ReadOnlyMemcachedNaturalIdRegionAccessStrategy(this, settings);
    }

    public NaturalIdRegionAccessStrategy getReadWriteRegionAccessStrategy(Settings settings) {
        return new ReadWriteMemcachedNaturalIdRegionAccessStrategy(this, settings, getCacheDataDescription());
    }

    public NaturalIdRegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy(Settings settings) {
        return new NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy(this, settings);
    }

    public NaturalIdRegionAccessStrategy getTransactionalRegionAccessStrategy(Settings settings) {
        return new TransactionalMemcachedNaturalIdRegionAccessStrategy(this, settings, getCacheDataDescription());
    }

}
