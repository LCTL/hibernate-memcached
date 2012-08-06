package com.googlecode.hibernate.memcached.region;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcachedCache;

public class MemcachedNaturalIdRegion 
    extends AbstractMemcachedTransactionalDataRegion implements NaturalIdRegion {

    public MemcachedNaturalIdRegion(MemcachedCache cache, Settings settings, CacheDataDescription metadata, Properties properties, Memcache client) {
        super(cache, settings, metadata);
    }

    @Override
    public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return (NaturalIdRegionAccessStrategy) super.buildAccessStrategy(accessType);
	}

    public RegionAccessStrategy getReadOnlyRegionAccessStrategy(Settings settings) {
        return null;
    }

    public RegionAccessStrategy getReadWriteRegionAccessStrategy(Settings settings) {
        return null;
    }

    public RegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy(Settings settings) {
        return null;
    }

    public RegionAccessStrategy getTransactionalRegionAccessStrategy(Settings settings) {
        return null;
    }

}
