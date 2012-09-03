package com.googlecode.hibernate.memcached

import com.googlecode.hibernate.memcached.MemcachedRegionProperties;
import com.googlecode.hibernate.memcached.client.spymemcached.*;
import com.googlecode.hibernate.memcached.client.dangamemcached.DangaMemcacheClientFactory;
import com.googlecode.hibernate.memcached.strategy.key.*
import com.googlecode.hibernate.memcached.strategy.key.encoding.*
/**
 * DOCUMENT ME!
 * @author Ray Krueger
 */
class MemcachedRegionPropertiesTest extends BaseTestCase {

    MemcachedRegionProperties newRegionProperties(Properties props) {
        new MemcachedRegionProperties(props)
    }

    void test_cache_time_seconds() {
        Properties p = new Properties()
        p["hibernate.memcached.cacheTimeSeconds"] = "10"
        p["hibernate.memcached.REGION.cacheTimeSeconds"] = "20"

        MemcachedRegionProperties regionProperties = newRegionProperties(p)
        assertEquals 10, regionProperties.getCacheTimeSeconds(null)
        assertEquals 20, regionProperties.getCacheTimeSeconds("REGION")
    }

    void test_clear_supported() {

        Properties p = new Properties()
        p["hibernate.memcached.clearSupported"] = "true"
        p["hibernate.memcached.REGION.clearSupported"] = "false"

        MemcachedRegionProperties regionProperties = newRegionProperties(p)
        assertTrue regionProperties.isClearSupported(null)
        assertFalse regionProperties.isClearSupported("REGION")
    }

    void test_key_encoding_strategy() {

        Properties p = new Properties()
        p["hibernate.memcached.keyEncodingStrategy"] = Md5KeyEncodingStrategy.class.getName()
        p["hibernate.memcached.REGION.keyEncodingStrategy"] = NonEncodingKeyEncodingStrategy.class.getName()

        MemcachedRegionProperties regionProperties = newRegionProperties(p)
        assertEquals Md5KeyEncodingStrategy.class.getName(), regionProperties.getKeyEncodingStrategy(null).class.getName()
        assertEquals NonEncodingKeyEncodingStrategy.class.getName(), regionProperties.getKeyEncodingStrategy("REGION").class.getName()
    }

    void test_dogpile_prevention() {

        Properties p = new Properties()
        p["hibernate.memcached.dogpilePrevention"] = "true"
        p["hibernate.memcached.REGION.dogpilePrevention"] = "false"

        MemcachedRegionProperties regionProperties = newRegionProperties(p)
        assertTrue regionProperties.isDogpilePreventionEnabled(null)
        assertFalse regionProperties.isDogpilePreventionEnabled("REGION")
    }

    void test_dogpile_prevention_expiration_factor() {
        Properties p = new Properties()
        p["hibernate.memcached.dogpilePrevention.expirationFactor"] = "10"
        p["hibernate.memcached.REGION.dogpilePrevention.expirationFactor"] = "20"

        MemcachedRegionProperties regionProperties = newRegionProperties(p)
        assertEquals 10, regionProperties.getDogpilePreventionExpirationFactor(null)
        assertEquals 20, regionProperties.getDogpilePreventionExpirationFactor("REGION")
    }

    // this is a MemcachedProperties property, but this is a good test for StringUtils.newInstance
    void test_memcache_client_factory() {
        Properties p = new Properties()
        MemcachedRegionProperties regionProperties = newRegionProperties(p)

        //test default
        assertEquals SpyMemcacheClientFactory.class.getName(),
                regionProperties.getMemcachedClientFactory().class.getName()

        p["hibernate.memcached.memcacheClientFactory"] = DangaMemcacheClientFactory.class.getName()

        assertEquals(DangaMemcacheClientFactory.class.getName(),
            regionProperties.getMemcachedClientFactory().class.getName()
        )


    }
}
