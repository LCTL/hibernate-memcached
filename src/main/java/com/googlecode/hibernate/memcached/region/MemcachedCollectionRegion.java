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
package com.googlecode.hibernate.memcached.region;

import java.util.Properties;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.NonStrictReadWriteMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadOnlyMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadWriteMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.TransactionalMemcachedCollectionRegionAccessStrategy;

/**
 *
 * @author kcarlson
 */
public class MemcachedCollectionRegion
    extends AbstractMemcachedTransactionalDataRegion<CollectionRegionAccessStrategy>
    implements CollectionRegion {
    
    private final Logger log = LoggerFactory.getLogger(MemcachedCollectionRegion.class);

    public MemcachedCollectionRegion(MemcachedCache cache, Settings settings,
            CacheDataDescription metadata, Properties properties, HibernateMemcachedClient client) {
        super(cache, settings, metadata);
    }

    public CollectionRegionAccessStrategy getReadOnlyRegionAccessStrategy(Settings settings) {
        return new ReadOnlyMemcachedCollectionRegionAccessStrategy(this, settings);
    }

    public CollectionRegionAccessStrategy getReadWriteRegionAccessStrategy(Settings settings) {
        return new ReadWriteMemcachedCollectionRegionAccessStrategy(this, settings);
    }

    public CollectionRegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy(Settings settings) {
        return new NonStrictReadWriteMemcachedCollectionRegionAccessStrategy(this, settings);
    }

    public CollectionRegionAccessStrategy getTransactionalRegionAccessStrategy(Settings settings) {
        return new TransactionalMemcachedCollectionRegionAccessStrategy(this, cache, settings);
    }

}