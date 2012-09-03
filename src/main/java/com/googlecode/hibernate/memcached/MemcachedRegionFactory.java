/* Copyright 2008 Ray Krueger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.hibernate.memcached;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;
import com.googlecode.hibernate.memcached.client.dangamemcached.DangaMemcacheClientFactory;
import com.googlecode.hibernate.memcached.client.spymemcached.SpyMemcachedProperties;
import com.googlecode.hibernate.memcached.region.MemcachedCollectionRegion;
import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;
import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;
import com.googlecode.hibernate.memcached.region.MemcachedQueryResultsRegion;
import com.googlecode.hibernate.memcached.region.MemcachedTimestampsRegion;
import com.googlecode.hibernate.memcached.strategy.clear.MemcachedRegionClearStrategy;


/**
 * An implementation of {@link RegionFactory} to add support for Memcached as
 * as second-level cache.
 * <p>
 * To use set the hibernate property <i>hibernate.cache.provider_class</i> to
 * <i>com.googlecode.hibernate.memcached.MemcachedRegionFactory</i>.
 * <p>
 * This {@link RegionFactory} supports three types of properties, cache-wide
 * properties (See {@link MemcachedProperties}), region-wide properties (See 
 * {@link MemcachedRegionProperties}), and client-wide properties (See 
 * {@link SpyMemcachedProperties} and {@link DangaMemcacheClientFactory}).
 * 
 * @author kcarlson
 */
public class MemcachedRegionFactory implements RegionFactory {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(MemcachedRegionFactory.class);

    private HibernateMemcachedClient client;
    private Settings hibernateSettings;
    
    /**
     * Creates a new {@link MemcachedRegionFactory}.
     */
    public MemcachedRegionFactory() { }
    
    /**
     * Creates a new {@link MemcachedRegionFactory}.
     * 
     * @param properties properties used to initialize the factory
     */
    public MemcachedRegionFactory(Properties properties) {
        super();
    }
    
    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        this.hibernateSettings = settings;
        
        try {
            log.info("Starting HibernateMemcachedClient...");
            client = buildHibernateMemcachedClient(properties);
            log.info("HibernateMemcachedClient started!");
        } catch (Exception e) {
            throw new CacheException("Unable to initialize HibernateMemcachedClient", e);
        }
    }

    @Override
    public void stop() {
        if (client != null) {
            log.debug("Shutting down Memcache client");
            client.shutdown();
        }
        client = null;
    }

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        // use hibernateSettings? settings.isMinimalPutsEnabled();
        return true;
    }

    @Override
    public AccessType getDefaultAccessType() {
         return AccessType.READ_WRITE;
    }

    @Override
    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new MemcachedEntityRegion(client, buildMemcachedRegionSettings(regionName, properties), metadata);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new MemcachedCollectionRegion(client, buildMemcachedRegionSettings(regionName, properties),  metadata);
    }
    
    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new MemcachedNaturalIdRegion(client, buildMemcachedRegionSettings(regionName, properties), metadata);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new MemcachedQueryResultsRegion(client, buildMemcachedRegionSettings(regionName, properties));
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new MemcachedTimestampsRegion(client, buildMemcachedRegionSettings(regionName, properties));
    }
    
    /**
     * Builds the {@link MemcachedRegionSettings} for a region using the given
     * {@link Properties}.
     * 
     * @param regionName the name of the region to get the settings for
     * @param properties the properties used to build the settings
     * @return           the settings for the given region
     */
    private MemcachedRegionSettings buildMemcachedRegionSettings(String regionName, Properties properties) {
        MemcachedRegionProperties regionProperties = new MemcachedRegionProperties(properties);
        MemcachedRegionSettings regionSettings = new MemcachedRegionSettings(regionName, regionProperties);
        regionSettings.setClearStrategy(new MemcachedRegionClearStrategy(client, regionSettings));
        regionSettings.setHibernateSettings(hibernateSettings);
        return regionSettings;
    }
    
    /**
     * Builds the {@link HibernateMemcachedClient} used to access Memcached.
     * 
     * @param properties the properties used to build the client
     * @return           a {@link HibernateMemcachedClient}
     */
    private HibernateMemcachedClient buildHibernateMemcachedClient(Properties properties) {
        MemcachedProperties memcachedProperties = new MemcachedProperties(properties);
        HibernateMemcachedClientFactory clientFactory = memcachedProperties.getMemcachedClientFactory();
        return clientFactory.createMemcacheClient();
    }
}
