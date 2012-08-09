package com.googlecode.hibernate.memcached;

import com.googlecode.hibernate.memcached.strategy.key.*
import com.googlecode.hibernate.memcached.region.*
import org.hibernate.cache.spi.CacheDataDescription
import org.hibernate.cache.internal.CacheDataDescriptionImpl
import org.hibernate.cfg.Settings
import java.util.Properties

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
class MemcachedRegionFactoryTest extends BaseTestCase {

    MemcachedRegionFactory regionFactory
    Properties properties
    Settings settings
    CacheDataDescription metadata

    void setUp() {
        regionFactory = new MemcachedRegionFactory()
        properties = new Properties()
        settings = new Settings()
        metadata =  new CacheDataDescriptionImpl(false, false, null)
    }

    // all these tests should probably just be moved to MemcachedCacheTest.groovy
    // since that is what they are really testing
    // or the methods should be moved into the client factory
    void test_defaults() {
        regionFactory.start(settings, properties)
        MemcachedQueryResultsRegion region = (MemcachedQueryResultsRegion) regionFactory.buildQueryResultsRegion("test", properties)
        MemcachedCache cache = region.getCache()
        assertNotNull(cache)

        //assert Defaults
        assertFalse(cache.isClearSupported())
        assertEquals(300, cache.getCacheTimeSeconds())
        assertEquals Sha1KeyStrategy.class, cache.getKeyStrategy().class
    }

    void test_region_properties() {
        properties.setProperty "hibernate.memcached.serverList", "127.0.0.1:11211"
        properties.setProperty "hibernate.memcached.test.cacheTimeSeconds", "500"
        properties.setProperty "hibernate.memcached.test.clearSupported", "true"
        properties.setProperty "hibernate.memcached.test.keyStrategy", StringKeyStrategy.class.getName()

        regionFactory.start(settings, properties)
        MemcachedQueryResultsRegion region = (MemcachedQueryResultsRegion) regionFactory.buildQueryResultsRegion("test", properties)
        MemcachedCache cache = region.getCache()
        assertNotNull(cache)

        //assert Defaults
        assertTrue(cache.isClearSupported())
        assertEquals(500, cache.getCacheTimeSeconds())
        assertEquals(StringKeyStrategy.class, cache.getKeyStrategy().class)
    }

    void test_string_key_strategy() {
        properties.setProperty("hibernate.memcached.keyStrategy", StringKeyStrategy.class.getName())

        regionFactory.start(settings, properties)
        MemcachedQueryResultsRegion region = (MemcachedQueryResultsRegion) regionFactory.buildQueryResultsRegion("test", properties)
        MemcachedCache cache = region.getCache()
        assertNotNull(cache)
    }

    void tearDown() {
        regionFactory.stop()
    }
}
