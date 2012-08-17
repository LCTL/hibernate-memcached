package com.googlecode.hibernate.memcached.concurrent.keylock;

public interface ReadWriteKeyLockProvider {

    public boolean acquireReadLock(String key);

    public boolean releaseReadLock(String key);

    public boolean acquireWriteLock(String key);

    public boolean releaseWriteLock(String key);

}
