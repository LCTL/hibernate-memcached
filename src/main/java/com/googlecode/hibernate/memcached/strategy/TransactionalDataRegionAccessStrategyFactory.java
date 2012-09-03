package com.googlecode.hibernate.memcached.strategy;

import org.hibernate.cache.spi.access.RegionAccessStrategy;

public interface TransactionalDataRegionAccessStrategyFactory <S extends RegionAccessStrategy>{

    public S getReadOnlyRegionAccessStrategy();
    
    public S getReadWriteRegionAccessStrategy();
    
    public S getNonStrictReadWriteRegionAccessStrategy();
    
    public S getTransactionalRegionAccessStrategy();
}