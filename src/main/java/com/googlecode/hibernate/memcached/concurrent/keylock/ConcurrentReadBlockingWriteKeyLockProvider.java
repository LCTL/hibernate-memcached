package com.googlecode.hibernate.memcached.concurrent.keylock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.strategy.key.encoding.KeyEncodingStrategy;
import com.googlecode.hibernate.memcached.utils.MemcachedRegionSettingsUtils;
import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * Provides Read/Write locking to Memcached on a per key basis (for a given
 * clear index) using Memcached as store for key locks. This implementation
 * allows for concurrent reads and blocking writes. That is, it will grant read
 * access to multiple requesters simultaneity, but only a single requester will
 * be granted write access at any given time. Additionally, write access will
 * not be granted until all read locks are released. (does this create too much
 * contention?).
 * <p>
 * <b>Implementation notes:</b>
 * <p>
 * In order to obtain any lock (read or write) on a given key, first the write
 * lock must be obtained (this is done using Memcached's add functionality). 
 * Once the write lock has been obtained a read lock request will increment the
 * number of readers and release (delete) the write lock, a write lock request
 * will wait for all current readers to finish before granting access to the
 * caller. In order to release it's lock a reader will decrement the reader 
 * count, a writer will delete the write lock.
 * <p>
 * Can locks be pushed out of memcached if there is too much usage? maybe
 * there is some way to have a separate memcached server for locks?
 * <p>
 * Does this implementation make sense for what is needed? Would it be safe
 * to allow all read requests to proceed while there was a write request?
 * This implementation mostly assumes that that the requested lock must be
 * acquired before returning (though it isn't really guaranteed), maybe just
 * don't grant read access when write lock exists? Reads would be faster this
 * way (if there are few writes). Is it faster to sleep and retry or go to the db?
 * (maybe db would be faster, but would increase load unnecessarily?)
 * I should probably make this a region parameter.
 */
public class ConcurrentReadBlockingWriteKeyLockProvider implements MemcachedReadWriteKeyLockProvider {

    private static final Logger log = LoggerFactory.getLogger(ConcurrentReadBlockingWriteKeyLockProvider.class);

    private HibernateMemcachedClient client;
    private MemcachedRegionSettings settings;
    private long clearIndex;
    
    private String fullReadLockKeyPrefix;
    private String fullWriteLockKeyPrefix;
    private KeyEncodingStrategy keyEncodingStrategy;

    /* Make default constructor private, or could add getters/setters */
    @SuppressWarnings("unused")
    private ConcurrentReadBlockingWriteKeyLockProvider() { }
    
    /**
     * Public constructor.
     * 
     * @param client         the client used to access memcached
     * @param settings       the settings used to initialize this lock provider
     */
    public ConcurrentReadBlockingWriteKeyLockProvider(HibernateMemcachedClient client, MemcachedRegionSettings settings, long clearIndex) {
        this.client = client;
        this.settings = settings;
        this.clearIndex = clearIndex;

        this.fullReadLockKeyPrefix = 
                MemcachedRegionSettingsUtils.getFullReadLockKeyPrefix(settings);
        this.fullWriteLockKeyPrefix = 
                MemcachedRegionSettingsUtils.getFullWriteLockKeyPrefix(settings);
        this.keyEncodingStrategy = 
                MemcachedRegionSettingsUtils.getValidatedMemcachedKeyEncodingStrategy(settings);
    }

    @Override
    public boolean acquireReadLock(String key) {
        boolean result = false;
        
        String wKey = getWriteLockKey(key);
        String rKey = getReadLockKey(key);

        // Add a counter of some kind?
        while(!client.add(wKey, 60, 1)) { // should probably parameterize cache and sleep time
            log.info("could not acquire read lock for key {}.", key);
            sleep(1000);
        }

        client.incr(rKey, 1, 0);
        result = true;
        client.delete(wKey); // only want to delete if still mine // set wKey to uuid then get before delete? safe, but adds another get... // maybe use thread name?
        
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
            log.info("waiting for reads to finish."); // do i really need to do this?
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
    
    /**
     * Generates a read lock key for the given object key. In this case the
     * key will map to a counter that will be incremented and decremented.
     * 
     * @param key a key for the requested object
     * @return    a key for the read lock
     */
    private String getReadLockKey(String key) {
        return keyEncodingStrategy.encode(concatenateKey(fullReadLockKeyPrefix, key));
    }
    
    /**
     * Generates a write lock key for the given object key. (Should this key
     * map to some particular kind of object, right now it is just an integer.)
     * 
     * @param key a key for the requested object
     * @return    a key for the write lock
     */
    private String getWriteLockKey(String key) {
        return keyEncodingStrategy.encode(concatenateKey(fullWriteLockKeyPrefix, key));
    }
    
    private String concatenateKey(String prefix, String key) {
        return StringUtils.join(settings.getNamespaceSeparator(), prefix, clearIndex, key);
    }

    /**
     * Puts the thread to sleep for some number of milliseconds.
     * 
     * @param millis the number of milliseconds to sleep.
     */
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    
}
