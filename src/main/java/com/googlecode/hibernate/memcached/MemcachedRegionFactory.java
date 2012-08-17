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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import com.googlecode.hibernate.memcached.concurrent.keylock.MemcachedRegionReadWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.region.MemcachedCollectionRegion;
import com.googlecode.hibernate.memcached.region.MemcachedEntityRegion;
import com.googlecode.hibernate.memcached.region.MemcachedNaturalIdRegion;
import com.googlecode.hibernate.memcached.region.MemcachedQueryResultsRegion;
import com.googlecode.hibernate.memcached.region.MemcachedTimestampsRegion;
import com.googlecode.hibernate.memcached.strategy.clear.MemcachedRegionClearStrategy;


/**
 *
 * @author kcarlson
 */
public class MemcachedRegionFactory implements RegionFactory {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(MemcachedRegionFactory.class);

    private HibernateMemcachedClient client;
    private MemcachedProperties properties;
    private Settings settings;
    
    public MemcachedRegionFactory() {
    }
    
    public MemcachedRegionFactory(Properties properties) {
        super();
        this.properties = new MemcachedProperties(properties);
    }
    
    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        log.info("Starting MemcachedClient...");
        
        this.settings = settings;
        this.properties = new MemcachedRegionProperties(properties);
        
        try {

            client = buildClient(this.properties);
        
        } catch (Exception e) {
            throw new CacheException("Unable to initialize MemcachedClient", e);
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
        // use settings? settings.isMinimalPutsEnabled();
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
        return new MemcachedEntityRegion(client, getRegionProperties(regionName, properties), settings, metadata);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new MemcachedCollectionRegion(client, getRegionProperties(regionName, properties), settings, metadata);
    }
    
    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new MemcachedNaturalIdRegion(client, getRegionProperties(regionName, properties), settings, metadata);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new MemcachedQueryResultsRegion(client, getRegionProperties(regionName, properties), settings);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new MemcachedTimestampsRegion(client, getRegionProperties(regionName, properties), settings);
    }
    
    private MemcachedRegionPropertiesHolder getRegionProperties(String region, Properties properties) {
        MemcachedRegionProperties props = new MemcachedRegionProperties(properties);
        MemcachedRegionPropertiesHolder holder = new MemcachedRegionPropertiesHolder(region, props);
        holder.setClearStrategy(new MemcachedRegionClearStrategy(client, holder));
        holder.setReadWriteKeyLockProvider(new MemcachedRegionReadWriteKeyLockProvider(client, holder));
        return holder;
    }
    
    private HibernateMemcachedClient buildClient(MemcachedProperties properties) throws Exception {
        HibernateMemcachedClientFactory clientFactory = properties.getMemcachedClientFactory();
        return clientFactory.createMemcacheClient();
    }
}
