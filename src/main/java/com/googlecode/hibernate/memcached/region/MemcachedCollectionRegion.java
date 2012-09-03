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

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.NonStrictReadWriteMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadOnlyMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadWriteMemcachedCollectionRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.TransactionalMemcachedCollectionRegionAccessStrategy;

/**
 * Implements the {@link CollectionRegion} interface.
 * 
 * @author kcarlson
 */
public class MemcachedCollectionRegion
    extends AbstractMemcachedTransactionalDataRegion<CollectionRegionAccessStrategy>
    implements CollectionRegion {
    
    private final Logger log = LoggerFactory.getLogger(MemcachedCollectionRegion.class);

    /**
     * Creates a new {@link MemcachedCollectionRegion}.
     * 
     * @param client               the client used to access Memcached
     * @param settings             the settings for this region
     * @param cacheDataDescription the metadata for this region
     */
    public MemcachedCollectionRegion(HibernateMemcachedClient client, MemcachedRegionSettings settings, CacheDataDescription cacheDataDescription) {
        super(client, settings, cacheDataDescription);
    }

    @Override
    public CollectionRegionAccessStrategy getReadOnlyRegionAccessStrategy() {
        return new ReadOnlyMemcachedCollectionRegionAccessStrategy(this);
    }

    @Override
    public CollectionRegionAccessStrategy getReadWriteRegionAccessStrategy() {
        return new ReadWriteMemcachedCollectionRegionAccessStrategy(this);
    }

    @Override
    public CollectionRegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy() {
        return new NonStrictReadWriteMemcachedCollectionRegionAccessStrategy(this);
    }

    @Override
    public CollectionRegionAccessStrategy getTransactionalRegionAccessStrategy() {
        return new TransactionalMemcachedCollectionRegionAccessStrategy(this);
    }

}