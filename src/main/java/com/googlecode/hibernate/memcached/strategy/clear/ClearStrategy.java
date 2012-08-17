package com.googlecode.hibernate.memcached.strategy.clear;

public interface ClearStrategy {

    boolean clear();
    
    long getClearIndex();
}
