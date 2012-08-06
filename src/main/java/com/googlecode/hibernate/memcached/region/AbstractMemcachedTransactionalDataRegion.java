package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.strategy.TransactionalDataRegionAccessStrategyFactory;

public abstract class AbstractMemcachedTransactionalDataRegion extends AbstractMemcachedRegion
    implements TransactionalDataRegion, TransactionalDataRegionAccessStrategyFactory {

    private static final Logger log = LoggerFactory.getLogger(AbstractMemcachedTransactionalDataRegion.class);
    
    private final CacheDataDescription metadata;
    private final Settings settings;

    public AbstractMemcachedTransactionalDataRegion(MemcachedCache cache, 
            Settings settings, CacheDataDescription metadata) {
        super(cache);
        this.metadata = metadata;
        this.settings = settings;
    }
    
    public RegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        
        switch(accessType) {
        case READ_ONLY:
            if (getCacheDataDescription().isMutable()) {
                log.warn("read-only cache configured for mutable entity [" + getName() + "]");
            }
            return getReadOnlyRegionAccessStrategy(getSettings());
        case READ_WRITE:
            return getReadWriteRegionAccessStrategy(getSettings());
        case NONSTRICT_READ_WRITE:
            return getNonStrictReadWriteRegionAccessStrategy(getSettings());
        case TRANSACTIONAL:
            return getTransactionalRegionAccessStrategy(getSettings());
        default:
            throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    public boolean isTransactionAware() {
        return false;
    }

    public CacheDataDescription getCacheDataDescription() {
        return metadata;
    }
    
    protected Settings getSettings() {
        return settings;
    }

}
