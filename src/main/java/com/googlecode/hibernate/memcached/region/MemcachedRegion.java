package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.spi.Region;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;
import com.googlecode.hibernate.memcached.concurrent.keylock.MemcachedReadWriteKeyLockProviderFactory;
import com.googlecode.hibernate.memcached.concurrent.keylock.MemcachedReadWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;

/**
 * An interface for operations on a particular Memcached region.
 * <p>
 * Should a MemcachedRegion implement the {@link KeyStrategy} interface?
 * 
 * @see org.hibernate.cache.spi.Region
 * @see HibernateMemcachedClient
 */
public interface MemcachedRegion extends Region, KeyStrategy {
    
    /**
     * Removes all keys from this <code>MemcachedRegion</code>.
     * 
     * @return <code>true</code> if the <code>MemcachedRegion</code> was 
     *         successfully cleared, <code>false</code> otherwise
     */
    public boolean clear();

    /**
     * Gets the {@link MemcachedRegionSettings} for this region.
     * 
     * @return the settings for this region
     */
    public MemcachedRegionSettings getSettings();
    
    /**
     * Creates a new {@link MemcachedRegionComponentFactory}.
     * 
     * @return a new {@link MemcachedRegionComponentFactory}
     */
    public MemcachedRegionComponentFactory createComponentFactory();
}
