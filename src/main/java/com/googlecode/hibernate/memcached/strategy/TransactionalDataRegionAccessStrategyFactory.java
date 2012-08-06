package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cfg.Settings;

public interface TransactionalDataRegionAccessStrategyFactory {

    public RegionAccessStrategy getReadOnlyRegionAccessStrategy(Settings settings);
    
    public RegionAccessStrategy getReadWriteRegionAccessStrategy(Settings settings);
    
    public RegionAccessStrategy getNonStrictReadWriteRegionAccessStrategy(Settings settings);
    
    public RegionAccessStrategy getTransactionalRegionAccessStrategy(Settings settings);
}