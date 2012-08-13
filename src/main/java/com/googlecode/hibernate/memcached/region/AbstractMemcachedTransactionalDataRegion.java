package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cache.spi.access.UnknownAccessTypeException;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.MemcachedRegionPropertiesHolder;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.TransactionalDataRegionAccessStrategyFactory;

public abstract class AbstractMemcachedTransactionalDataRegion<S extends RegionAccessStrategy>
    extends AbstractMemcachedRegion
    implements TransactionalDataRegion, TransactionalDataRegionAccessStrategyFactory<S> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMemcachedTransactionalDataRegion.class);
    
    private final CacheDataDescription metadata;

    public AbstractMemcachedTransactionalDataRegion(HibernateMemcachedClient client, MemcachedRegionPropertiesHolder properties, Settings settings, CacheDataDescription metadata) {
        super(client, properties, settings);
        this.metadata = metadata;
    }
    
    public S buildAccessStrategy(AccessType accessType) throws CacheException {
        
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
            throw new UnknownAccessTypeException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    public boolean isTransactionAware() {
        return false;
    }

    public CacheDataDescription getCacheDataDescription() {
        return metadata;
    }
}
