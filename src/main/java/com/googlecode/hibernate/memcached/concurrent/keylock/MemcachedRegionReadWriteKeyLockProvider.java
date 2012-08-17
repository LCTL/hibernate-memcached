package com.googlecode.hibernate.memcached.concurrent.keylock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionPropertiesHolder;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.clear.ClearStrategy;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;

public class MemcachedRegionReadWriteKeyLockProvider implements ReadWriteKeyLockProvider {

    private static final Logger log = LoggerFactory.getLogger(MemcachedRegionReadWriteKeyLockProvider.class);

    private HibernateMemcachedClient client;
    
    private KeyStrategy keyStrategy;
    private ClearStrategy clearStrategy;
    
    private String readLockKeyPrefix;
    private String writeLockKeyPrefix;

    public MemcachedRegionReadWriteKeyLockProvider(HibernateMemcachedClient client,
            MemcachedRegionPropertiesHolder regionProperties) {
        this.client = client;
        
        this.keyStrategy = regionProperties.getKeyStrategy();
        this.clearStrategy = regionProperties.getClearStrategy();
        
        this.readLockKeyPrefix = new StringBuilder()
            .append(regionProperties.getReadLockKeyPrefix())
            .append(regionProperties.getNamespaceSeparator())
            .append(regionProperties.getName())
            .toString();
        
        this.writeLockKeyPrefix = new StringBuilder()
            .append(regionProperties.getWriteLockKeyPrefix())
            .append(regionProperties.getNamespaceSeparator())
            .append(regionProperties.getName())
            .toString();
    }

    @Override
    public boolean acquireReadLock(String key) {
        boolean result = false;
        
        String wKey = getWriteLockKey(key);
        String rKey = getReadLockKey(key);

        // Add a counter of some kind?
        while(!client.add(wKey, 60, 1)) {
            log.info("could not acquire read lock for key {}.", key);
            sleep(1000);
        }

        client.incr(rKey, 1, 0);
        result = true;
        client.delete(wKey); // only want to delete if still mine // set wKey to uuid then get before delete? safe, but adds another get...
        
        log.trace("got read lock for key {}.", key);
        return result;
    }

    @Override
    public boolean releaseReadLock(String key) {
        String rKey = getReadLockKey(key);
        long value = client.decr(rKey, 1, 0);
        log.trace("release read lock for key {}.", key);
        // if value < 0 set to 0
        return true;
    }

    @Override
    public boolean acquireWriteLock(String key) {
        boolean result = false;
        String wKey = getWriteLockKey(key);
        String rKey = getReadLockKey(key);
        
        while (!client.add(wKey, 60, 1)) {
            log.info("could not acquire write lock for key {}.", key);
            sleep(1000);
        }
        
        log.trace("acquired write lock for key {}.", key);
        
        result = true;
        while (((Integer) client.get(rKey)) > 0) {
            log.info("waiting for reads to finish.");
            sleep(1000);
        }
        return result;
    }

    @Override
    public boolean releaseWriteLock(String key) {
        String wKey = getWriteLockKey(key);
        client.delete(wKey);
        log.trace("release wriet lock for key {}.", key);
        return true;
    }
    
    private String getReadLockKey(String key) {
        return keyStrategy.toKey(readLockKeyPrefix, clearStrategy.getClearIndex(), key);
    }
    
    private String getWriteLockKey(String key) {
        return keyStrategy.toKey(writeLockKeyPrefix, clearStrategy.getClearIndex(), key);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    
}
