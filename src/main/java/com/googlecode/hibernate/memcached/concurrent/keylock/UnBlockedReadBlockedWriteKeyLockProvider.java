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
 * allows for un blocked reads and blocking writes. That is, it will always 
 * grant read access to any request, but only a single requester will be 
 * granted write access at any given time.
 * <p>
 * <b>Implementation notes:</b>
 * <p>
 * In order to obtain the write lock on a given key Memcached's add 
 * functionality is used. The first requester to successfully add the full lock
 * key to Memcached will be the one to have the write lock for that key. 
 * Requesters that fail to acquire the lock will sleep and try again (could lead
 * to starvation?). In order to release it's lock a writer will delete the 
 * full lock key from Memcached.
 * <p>
 * Can locks be pushed out of memcached if there is too much usage? maybe
 * there is some way to have a separate memcached server for locks?
 * <p>
 * Does this implementation make sense for what is needed? Would is it safe
 * to allow all read requests to proceed while there was a write request?
 * This implementation mostly assumes that that the requested lock must be
 * acquired before returning (though it isn't really guaranteed). Is it faster
 * to sleep and retry or go to the db? (maybe db would be faster, but would
 * increase load unnecessarily?) I should probably make this a region parameter.
 */
public class UnBlockedReadBlockedWriteKeyLockProvider implements MemcachedReadWriteKeyLockProvider {

    private static final Logger log = LoggerFactory.getLogger(UnBlockedReadBlockedWriteKeyLockProvider.class);

    private HibernateMemcachedClient client;
    private MemcachedRegionSettings settings;
    private long clearIndex;
    
    private String fullWriteLockKeyPrefix;
    private KeyEncodingStrategy keyEncodingStrategy;

    /* Make default constructor private, or could add getters/setters */
    @SuppressWarnings("unused")
    private UnBlockedReadBlockedWriteKeyLockProvider() { }
    
    /**
     * Public constructor.
     * 
     * @param client         the client used to access memcached
     * @param settings       the settings used to initialize this lock provider
     */
    public UnBlockedReadBlockedWriteKeyLockProvider(HibernateMemcachedClient client, MemcachedRegionSettings settings, long clearIndex) {
        this.client = client;
        this.settings = settings;
        this.clearIndex = clearIndex;

        this.fullWriteLockKeyPrefix = 
                MemcachedRegionSettingsUtils.getFullWriteLockKeyPrefix(settings);
        this.keyEncodingStrategy = 
                MemcachedRegionSettingsUtils.getValidatedMemcachedKeyEncodingStrategy(settings);
    }

    @Override
    public boolean acquireReadLock(String key) {
        return true;
    }

    @Override
    public boolean releaseReadLock(String key) {
        return true;
    }

    @Override
    public boolean acquireWriteLock(String key) {
        String wKey = getWriteLockKey(key);
        
        boolean success = client.add(wKey, 60, 1);
        while (!success) {
            log.info("could not acquire write lock for key {}.", key);
            sleep(1000);
            success = client.add(wKey, 60, 1);
        }
        
        log.trace("acquired write lock for key {}.", key);
        return success;
    }

    @Override
    public boolean releaseWriteLock(String key) {
        String wKey = getWriteLockKey(key);
        boolean success = client.delete(wKey);
        if (!success)
            log.warn("could not release write lock for key {}.", key);
        return success;
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
