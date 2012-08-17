/* Copyright 2008 Ray Krueger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.hibernate.memcached.strategy;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.region.MemcachedRegion;

/**
 *
 * @author kcarlson
 */
public class AbstractReadWriteMemcachedAccessStrategy <T extends MemcachedRegion>
    extends AbstractMemcachedRegionAccessStrategy<T> {
 
    private static final Logger log = LoggerFactory.getLogger(AbstractReadWriteMemcachedAccessStrategy.class);
     
    private final AtomicLong nextLockId = new AtomicLong();
    private final UUID uuid = UUID.randomUUID();
     
    private final Comparator versionComparator;
 
    /**
     * Creates a read/write cache access strategy around the given cache region.
     */
    public AbstractReadWriteMemcachedAccessStrategy(T region, Settings settings, CacheDataDescription cacheDataDescription) {
        super(region, settings);
        this.versionComparator = cacheDataDescription.getVersionComparator();
    }
 
    /**
     * {@inheritDoc}</br>
     * Returns <code>null</code> if the item is not readable.
     * Locked items are not readable, nor are items created
     * after the start of this transaction.
     */
    @Override
    public final Object get(Object key, long txTimestamp) throws CacheException {
        String objectKey = String.valueOf(key);
        MemcachedRegion region = getRegion();
        region.acquireReadLock(objectKey);
        
        try {
            Lockable item = (Lockable) region.get(objectKey);
            boolean readable = item != null && item.isReadable(txTimestamp);
            if (readable) {
                return item.getValue();
            } else {
                return null;
            }
        } finally {
            region.releaseReadLock(objectKey);
        }
    }
 
    /**
     * {@inheritDoc}</br>
     * Soft-lock a cache item.
     */
    public final SoftLock lockItem(Object key, Object version) throws CacheException {
        String objectKey = String.valueOf(key);
        MemcachedRegion region = getRegion();
        region.acquireWriteLock(objectKey);
        
        try {
            Lockable item = (Lockable) region.get(objectKey); // problem here? t1:get t2:get t1:set t2:set
            long timeout = region.nextTimestamp() + region.getTimeout();
            final Lock lock = (item == null) ? new Lock(timeout, uuid, nextLockId(), version) : item.lock(timeout, uuid, nextLockId());
            region.set(objectKey, lock);
            return lock;
        } finally {
            region.releaseWriteLock(objectKey);
        }
    }
 
    /**
     * {@inheritDoc}</br>
     * Returns <code>false</code> and fails to put the value if there is an 
     * existing un-writeable item mapped to this key. 
     */
    @Override
    public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        String objectKey = String.valueOf(key);
        MemcachedRegion region = getRegion();
        region.acquireWriteLock(objectKey);
        
        try {
            Lockable item = (Lockable) region.get(objectKey);
            boolean writeable = item == null || item.isWriteable(txTimestamp, version, versionComparator);
            if (writeable) {
                region.set(objectKey, new Item(value, version, region.nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            region.releaseWriteLock(objectKey);
        }
    }
 
    /**
     * {@inheritDoc}</br>
     * Soft-unlock a cache item.
     */
    public final void unlockItem(Object key, SoftLock lock) throws CacheException {
        String objectKey = String.valueOf(key);
        MemcachedRegion region = getRegion();
        region.acquireWriteLock(objectKey);
        
        try {
            Lockable item = (Lockable) region.get(objectKey);
            boolean unlockable = item != null && item.isUnlockable(lock);
            if (unlockable) {
                decrementLock(objectKey, (Lock) item);
            } else {
                handleLockExpiry(objectKey, item);
            }
        } finally {
            region.releaseWriteLock(objectKey);
        }
    }
 
    /**
     * Unlock and re-put the given key, lock combination.
     */
    protected void decrementLock(String objectKey, Lock lock) {
        lock.unlock(getRegion().nextTimestamp());
        getRegion().set(objectKey, lock);
    }
 
    /**
     * Handle the timeout of a previous lock mapped to this key
     */
    protected void handleLockExpiry(String objectKey, Lockable lock) {
        log.warn("Cache " + getRegion().getName() + " Key " + objectKey + " Lockable : " + lock + "\n"
                + "A soft-locked cache entry was expired by the underlying Memcache. "
                + "If this happens regularly you should consider increasing the cache timeouts and/or capacity limits");
        long timestamp = getRegion().nextTimestamp();
        long timeout = timestamp + getRegion().getTimeout();
        // create new lock that times out immediately
        Lock newLock = new Lock(timeout, uuid, nextLockId.getAndIncrement(), null);
        //newLock.unlock(timestamp); // should this be just nextTimestamp (now)?
        getRegion().set(objectKey, newLock);
    }
 
    private long nextLockId() {
        return nextLockId.getAndIncrement();
    }

}