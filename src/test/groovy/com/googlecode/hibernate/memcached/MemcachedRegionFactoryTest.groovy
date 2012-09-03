package com.googlecode.hibernate.memcached;

import com.googlecode.hibernate.memcached.strategy.key.*
import com.googlecode.hibernate.memcached.strategy.key.encoding.*
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
        assertNotNull(region)

        //assert Defaults
        assertFalse(region.getSettings().isClearSupported())
        assertEquals(300, region.getSettings().getCacheTimeSeconds())
        assertEquals ToStringKeyStrategy.class, region.getSettings().getKeyStrategy().class
        assertEquals Sha1KeyEncodingStrategy.class, region.getSettings().getKeyEncodingStrategy().class
    }

    void test_region_properties() {
        properties.setProperty "hibernate.memcached.serverList", "127.0.0.1:11211"
        properties.setProperty "hibernate.memcached.test.cacheTimeSeconds", "500"
        properties.setProperty "hibernate.memcached.test.clearSupported", "true"
        properties.setProperty "hibernate.memcached.test.keyEncodingStrategy", NonEncodingKeyEncodingStrategy.class.getName()

        regionFactory.start(settings, properties)
        MemcachedQueryResultsRegion region = (MemcachedQueryResultsRegion) regionFactory.buildQueryResultsRegion("test", properties)
        assertNotNull(region)

        //assert Defaults
        assertTrue(region.getSettings().isClearSupported())
        assertEquals(500, region.getSettings().getCacheTimeSeconds())
        assertEquals ToStringKeyStrategy.class, region.getSettings().getKeyStrategy().class
        assertEquals NonEncodingKeyEncodingStrategy.class, region.getSettings().getKeyEncodingStrategy().class
    }

    void test_non_encoding_key_encoding_strategy() {
        properties.setProperty("hibernate.memcached.keyEncodingStrategy", NonEncodingKeyEncodingStrategy.class.getName())

        regionFactory.start(settings, properties)
        MemcachedQueryResultsRegion region = (MemcachedQueryResultsRegion) regionFactory.buildQueryResultsRegion("test", properties)
        assertNotNull(region)

        assertEquals NonEncodingKeyEncodingStrategy.class, region.getSettings().getKeyEncodingStrategy().class
    }

    void tearDown() {
        regionFactory.stop()
    }
}
