package com.googlecode.hibernate.memcached.region;

import java.util.Properties;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcachedCache;

public class MemcachedNaturalIdRegion 
    extends AbstractMemcachedTransactionalDataRegion<NaturalIdRegionAccessStrategy>
    implements NaturalIdRegion {

    public MemcachedNaturalIdRegion(MemcachedCache cache, Settings settings, CacheDataDescription metadata, Properties properties, Memcache client) {
        super(cache, settings, metadata);
    }

    public NaturalIdRegionAccessStrategy getReadOnlyRegionAccessStrategy(Settings settings) {
        return null;
    }

    public NaturalIdRegionAccessStrategy getReadWriteRegionAccessStrategy(Settings settings) {
        return null;
    }

    public NaturalIdRegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy(Settings settings) {
        return null;
    }

    public NaturalIdRegionAccessStrategy getTransactionalRegionAccessStrategy(Settings settings) {
        return null;
    }

}
