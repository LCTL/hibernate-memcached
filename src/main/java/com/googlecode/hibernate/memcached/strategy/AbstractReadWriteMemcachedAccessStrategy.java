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
import org.hibernate.cache.spi.access.RegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.concurrent.keylock.MemcachedReadWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.region.MemcachedRegion;
import com.googlecode.hibernate.memcached.region.MemcachedRegionComponentFactory;

/**
 * An abstract {@link RegionAccessStrategy} that supports key locking.
 * 
 * @author kcarlson
 * 
 * @param <T> the underling {@link MemcachedRegion} implementation type
 */
public class AbstractReadWriteMemcachedAccessStrategy <T extends MemcachedRegion>
    extends AbstractMemcachedRegionAccessStrategy<T> {
 
    private static final Logger log = LoggerFactory.getLogger(AbstractReadWriteMemcachedAccessStrategy.class);
     
    private final AtomicLong nextLockId = new AtomicLong();
    private final UUID uuid = UUID.randomUUID();
     
    private final Comparator<?> versionComparator;
 
    /**
     * Creates a read/write cache access strategy around the given cache region.
     */
    public AbstractReadWriteMemcachedAccessStrategy(T region, CacheDataDescription cacheDataDescription) {
        super(region);
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
        LockedExecution<Object> execution = new LockedExecution<Object>(txTimestamp) {
            private Object result = null;

            @Override
            public void exec(String objectKey, HibernateMemcachedClient client, MemcachedRegion region) {
                long txTimestamp = (Long) args[0];
                
                Lockable item = (Lockable) client.get(objectKey);
                boolean readable = item != null && item.isReadable(txTimestamp);
                if (readable) {
                    result = item.getValue();
                }
            }

            @Override
            public Object result() {
                return result;
            }
        };

        String objectKey = toKey(key);
        wrapWithReadLock(objectKey, execution);
    
        if (execution.result() == null) {
            log.info("Could not get item for key {}", objectKey);
        }
    
        return execution.result();
    }
 
    /**
     * {@inheritDoc}</br>
     * Soft-lock a cache item.
     */
    public final SoftLock lockItem(Object key, Object version) throws CacheException {
        LockedExecution<SoftLock> execution = new LockedExecution<SoftLock>(version) {
            private SoftLock lock = null;

            @Override
            public void exec(String objectKey, HibernateMemcachedClient client, MemcachedRegion region) {
                Object version = args[0];
                Lockable item = (Lockable) client.get(objectKey);
                long timeout = region.nextTimestamp() + region.getTimeout();
                lock = (item == null) ? new Lock(timeout, uuid, nextLockId(), version) : item.lock(timeout, uuid, nextLockId());
                client.set(objectKey, region.getTimeout(), lock);
            }

            @Override
            public SoftLock result() {
                return lock;
            }
        };
    
        String objectKey = toKey(key);
        wrapWithWriteLock(objectKey, execution);
    
        if (execution.result() == null) {
            log.info("Could not lockItem for key {}", objectKey);
        }
        
        return execution.result();
    }
 
    /**
     * {@inheritDoc}</br>
     * Returns <code>false</code> and fails to put the value if there is an 
     * existing un-writeable item mapped to this key. 
     */
    @Override
    public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        LockedExecution<Boolean> execution = new LockedExecution<Boolean>(value, txTimestamp, version) {
            private boolean success = false;

            @Override
            public void exec(String objectKey, HibernateMemcachedClient client, MemcachedRegion region) {
                Object value = args[0];    long txTimestamp = (Long) args[1];
                Object version = args[2];
                
                Lockable item = (Lockable) client.get(objectKey);
                boolean writeable = item == null || item.isWriteable(txTimestamp, version, versionComparator);
                if (writeable) {
                    success = client.set(objectKey, region.getTimeout(), new Item(value, version, region.nextTimestamp()));
                }
            }

            @Override
            public Boolean result() {
                return success;
            }
        };

        String objectKey = toKey(key);
        wrapWithWriteLock(objectKey, execution);
    
        if (!execution.result()) {
            log.warn("Could not putFromLoad item for key {}", objectKey);
        }
    
        return execution.result();
    }
 
    @Override
    public void remove(Object key) throws CacheException {
        LockedExecution<Boolean> execution = new LockedExecution<Boolean>() {
            private boolean success = false;

            @Override
            public void exec(String objectKey, HibernateMemcachedClient client, MemcachedRegion region) {
                success = client.delete(objectKey);
            }

            @Override
            public Boolean result() {
                return success;
            }
        };

        String objectKey = toKey(key);
        wrapWithWriteLock(objectKey, execution);
    
        if (!execution.result()) {
            log.warn("Could not remove item for key {}", objectKey);
        }
    }
 
    /**
     * {@inheritDoc}</br>
     * Soft-unlock a cache item.
     */
    public final void unlockItem(Object key, SoftLock lock) throws CacheException {
        LockedExecution<Boolean> execution = new LockedExecution<Boolean>(lock) {
            private boolean success = false;

            @Override
            public void exec(String objectKey, HibernateMemcachedClient client, MemcachedRegion region) {
                SoftLock lock = (SoftLock) args[0];
                Lockable item = (Lockable) client.get(objectKey);
                boolean unlockable = item != null && item.isUnlockable(lock);
                 if (unlockable) {
                    success = decrementLock(client, objectKey, (Lock) item);
                } else {
                    success = handleLockExpiry(client, objectKey, item);
                }
            }

            @Override
            public Boolean result() {
                return success;
            }
        };
    
        String objectKey = toKey(key);
        wrapWithWriteLock(objectKey, execution);
    
        if (!execution.result()) {
            log.info("Could not unlockItem for key {}", objectKey);
        }
    }
 
    /**
     * Unlock and re-put the given key, lock combination.
     */
    protected boolean decrementLock(HibernateMemcachedClient client, String objectKey, Lock lock) {
        lock.unlock(getRegion().nextTimestamp());
        return client.set(objectKey, getRegion().getTimeout(), lock);
    }
 
    /**
     * Handle the timeout of a previous lock mapped to this key
     */
    protected boolean handleLockExpiry(HibernateMemcachedClient client, String objectKey, Lockable lock) {
        log.warn("Cache " + getRegion().getName() + " Key " + objectKey + " Lockable : " + lock + "\n"
                + "A soft-locked cache entry was expired by the underlying Memcache. "
                + "If this happens regularly you should consider increasing the cache timeouts and/or capacity limits");
        long now = getRegion().nextTimestamp();
        // create new lock that times out immediately
        Lock newLock = new Lock(now, uuid, nextLockId(), null);
        newLock.unlock(now);
        return client.set(objectKey, getRegion().getTimeout(), newLock);
    }

    private long nextLockId() {
        return nextLockId.getAndIncrement();
    }
    
    protected void wrapWithReadLock(String objectKey, LockedExecution<?> exec) {
        MemcachedRegion region = getRegion();
        MemcachedRegionComponentFactory componentFactory = region.createComponentFactory();
        HibernateMemcachedClient client = componentFactory.createMemcacheClient();
        MemcachedReadWriteKeyLockProvider lockProvider = componentFactory.createMemcachedReadWriteKeyLockProvider();

        if (lockProvider.acquireReadLock(objectKey)) {
            try {
                exec.exec(objectKey, client, region);
            } finally {
                lockProvider.releaseReadLock(objectKey);
            }
        } else {
            log.info("Fail to acquire read lock for {}", objectKey);
        }
    }
    
    protected void wrapWithWriteLock(String objectKey, LockedExecution<?> exec) {
        MemcachedRegion region = getRegion();
        MemcachedRegionComponentFactory componentFactory = region.createComponentFactory();
        HibernateMemcachedClient client = componentFactory.createMemcacheClient();
        MemcachedReadWriteKeyLockProvider lockProvider = componentFactory.createMemcachedReadWriteKeyLockProvider();

        if (lockProvider.acquireWriteLock(objectKey)) {
            try {
                exec.exec(objectKey, client, region);
            } finally {
                lockProvider.releaseWriteLock(objectKey);
            }
        } else {
            log.info("Fail to acquire write lock for {}", objectKey);
        }
    }
    
    @SuppressWarnings("hiding")
    protected abstract class LockedExecution<T extends Object> {
        protected Object[] args;
        public LockedExecution(Object ... args) {
            this.args = args;
        }
        
        public abstract void exec(String objectKey, HibernateMemcachedClient client, MemcachedRegion region);
        public abstract T result();
    }

}