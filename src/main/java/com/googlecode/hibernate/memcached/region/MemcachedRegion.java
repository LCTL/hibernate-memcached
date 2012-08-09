package com.googlecode.hibernate.memcached.region;

import org.hibernate.cache.spi.Region;

import com.googlecode.hibernate.memcached.MemcachedCache;

public interface MemcachedRegion extends Region {

    public MemcachedCache getCache();
}
