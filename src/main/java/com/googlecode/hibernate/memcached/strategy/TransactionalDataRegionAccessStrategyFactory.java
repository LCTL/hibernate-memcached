package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cfg.Settings;

public interface TransactionalDataRegionAccessStrategyFactory <S extends RegionAccessStrategy> {

    public S getReadOnlyRegionAccessStrategy(Settings settings);
    
    public S getReadWriteRegionAccessStrategy(Settings settings);
    
    public S getNonStrictReadWriteRegionAccessStrategy(Settings settings);
    
    public S getTransactionalRegionAccessStrategy(Settings settings);
}