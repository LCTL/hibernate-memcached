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
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.NonStrictReadWriteMemcachedEntityRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadOnlyMemcachedEntityRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadWriteMemcachedEntityRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.TransactionalMemcachedEntityRegionAccessStrategy;
/**
 * Implements the {@link EntityRegion} interface.
 * 
 * @author kcarlson
 */
public class MemcachedEntityRegion
    extends AbstractMemcachedTransactionalDataRegion<EntityRegionAccessStrategy>
    implements EntityRegion {
    
    private final Logger log = LoggerFactory.getLogger(MemcachedEntityRegion.class);
    
    /**
     * Creates a new {@link MemcachedEntityRegion}.
     * 
     * @param client               the client used to access Memcached
     * @param settings             the settings for this region
     * @param cacheDataDescription the metadata for this region
     */
    public MemcachedEntityRegion(HibernateMemcachedClient client, MemcachedRegionSettings settings, CacheDataDescription cacheDataDescription) {
        super(client, settings, cacheDataDescription);
    }

    @Override
    public EntityRegionAccessStrategy getReadOnlyRegionAccessStrategy() {
        return new ReadOnlyMemcachedEntityRegionAccessStrategy(this);
    }

    @Override
    public EntityRegionAccessStrategy getReadWriteRegionAccessStrategy() {
        return new ReadWriteMemcachedEntityRegionAccessStrategy(this);
    }

    @Override
    public EntityRegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy() {
        return new NonStrictReadWriteMemcachedEntityRegionAccessStrategy(this);
    }

    @Override
    public EntityRegionAccessStrategy getTransactionalRegionAccessStrategy() {
        return new TransactionalMemcachedEntityRegionAccessStrategy(this);
    }

}
