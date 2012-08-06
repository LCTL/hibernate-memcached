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


import com.googlecode.hibernate.memcached.Memcache;
import com.googlecode.hibernate.memcached.MemcachedCache;
import com.googlecode.hibernate.memcached.MemcachedCacheProvider;
import com.googlecode.hibernate.memcached.strategy.NonStrictReadWriteMemcachedEntityRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadOnlyMemcachedEntityRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.ReadWriteMemcachedEntityRegionAccessStrategy;
import com.googlecode.hibernate.memcached.strategy.TransactionalMemcachedEntityRegionAccessStrategy;
import java.util.Properties;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.transaction.spi.AbstractTransactionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author kcarlson
 */
public class MemcachedEntityRegion
    extends AbstractMemcachedTransactionalDataRegion implements EntityRegion {
    
    private final Logger log = LoggerFactory.getLogger(MemcachedCacheProvider.class);
    
    public MemcachedEntityRegion(MemcachedCache cache, Settings settings, CacheDataDescription metadata, Properties properties, Memcache client) {
        super(cache, settings, metadata);
    }

    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return (EntityRegionAccessStrategy) super.buildAccessStrategy(accessType);
    }

    @Override
    public boolean isTransactionAware() {
        return true;
    }

    public RegionAccessStrategy getReadOnlyRegionAccessStrategy(Settings settings) {
        return new ReadOnlyMemcachedEntityRegionAccessStrategy(this, settings);
    }

    public RegionAccessStrategy getReadWriteRegionAccessStrategy(Settings settings) {
        return new ReadWriteMemcachedEntityRegionAccessStrategy(this, settings);
    }

    public RegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy(Settings settings) {
        return new NonStrictReadWriteMemcachedEntityRegionAccessStrategy(this, settings);
    }

    public RegionAccessStrategy getTransactionalRegionAccessStrategy(Settings settings) {
        return new TransactionalMemcachedEntityRegionAccessStrategy(this, cache, settings);
    }

}
