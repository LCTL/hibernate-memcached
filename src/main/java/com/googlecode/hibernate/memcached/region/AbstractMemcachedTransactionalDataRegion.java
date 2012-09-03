package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cache.spi.access.UnknownAccessTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.TransactionalDataRegionAccessStrategyFactory;

/**
 * An abstract implementation of {@link TransactionalDataRegion} and 
 * {@link TransactionalDataRegionAccessStrategyFactory}.
 * 
 * @param <S> extends {@link RegionAccessStrategy}
 */
public abstract class AbstractMemcachedTransactionalDataRegion <S extends RegionAccessStrategy>
    extends AbstractMemcachedRegion
    implements TransactionalDataRegion, TransactionalDataRegionAccessStrategyFactory<S> {

    private static final Logger log = LoggerFactory.getLogger(AbstractMemcachedTransactionalDataRegion.class);
    
    private final CacheDataDescription metadata;

    /**
     * Creates a new {@link AbstractMemcachedTransactionalDataRegion} with the
     * given client, settings and metadata.
     * 
     * @param client               the client used to access Memcached
     * @param settings             the settings for this region
     * @param cacheDataDescription the metadata for this region
     */
    public AbstractMemcachedTransactionalDataRegion(HibernateMemcachedClient client, MemcachedRegionSettings settings, CacheDataDescription cacheDataDescription) {
        super(client, settings);
        this.metadata = cacheDataDescription;
    }
    
    /**
     * Creates a new {@link RegionAccessStrategy} based on the given 
     * {@link AccessType}. This is the implementation of a method which is 
     * declared in many interfaces that extend {@link TransactionalDataRegion}.
     * 
     * @param accessType      the type of access allowed by the generated
     *                        {@link RegionAccessStrategy}
     * @return                a new {@link RegionAccessStrategy} with the
     *                        requested {@link AccessType}
     * @throws CacheException if something went wrong in the cache
     */
    public S buildAccessStrategy(AccessType accessType) throws CacheException {
        
        switch(accessType) {
        case READ_ONLY:
            if (metadata.isMutable()) {
                log.warn("{} cache configured for mutable entity [{}]", AccessType.READ_ONLY, getName());
            }
            return getReadOnlyRegionAccessStrategy();
            
        case READ_WRITE:
            if (!metadata.isMutable()) {
                log.warn("{} cache configured for imutable entity [{}]", AccessType.READ_WRITE, getName());
            }
            return getReadWriteRegionAccessStrategy();
            
        case NONSTRICT_READ_WRITE:
            if (!metadata.isMutable()) {
                log.warn("{} cache configured for imutable entity [{}]", AccessType.NONSTRICT_READ_WRITE, getName());
            }
            return getNonStrictReadWriteRegionAccessStrategy();
            
        case TRANSACTIONAL:
            if (!metadata.isMutable()) {
                log.warn("{} cache configured for imutable entity [{}]", AccessType.TRANSACTIONAL, getName());
            }
            return getTransactionalRegionAccessStrategy();
            
        default:
            throw new UnknownAccessTypeException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    @Override
    public boolean isTransactionAware() {
        return false;
    }

    /**
     * Gets the CacheDataDescription for this region.
     * 
     * @return the CacheDataDescription for this region, or null
     */
    @Override
    public CacheDataDescription getCacheDataDescription() {
        return metadata;
    }
}
