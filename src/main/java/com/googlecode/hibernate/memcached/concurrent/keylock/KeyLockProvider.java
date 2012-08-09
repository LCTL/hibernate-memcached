package com.googlecode.hibernate.memcached.concurrent.keylock;

public interface KeyLockProvider {

    public boolean acquire(String key, KeyLockType type);
    
    public boolean release(String key, KeyLockType type);
    
    public String getLockKey(String key, KeyLockType type);
}
