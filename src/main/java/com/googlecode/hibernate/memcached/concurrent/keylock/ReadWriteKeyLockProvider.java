package com.googlecode.hibernate.memcached.concurrent.keylock;

import java.rmi.UnexpectedException;

import com.googlecode.hibernate.memcached.MemcachedCache;

public class ReadWriteKeyLockProvider implements KeyLockProvider {
	
	public enum ReadWriteKeyLockType implements KeyLockType {
		READ, WRITE;
	}
	
	private MemcachedCache memcached;
	
	private String readKeyPrefix;
	private String writeKeyPrefix;
	private String separator;
	
	public ReadWriteKeyLockProvider(
			String readKeyPrefix, 
			String writeKeyPrefix,
			String separator) {
		this.readKeyPrefix = readKeyPrefix;
		this.writeKeyPrefix = writeKeyPrefix;
		this.separator = separator;
	}

	@Override
	public boolean acquire(String key, KeyLockType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean release(String key, KeyLockType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getLockKey(String key, KeyLockType type) {
		if (!(type instanceof ReadWriteKeyLockType)) {
			throw new IllegalArgumentException("Unexpected type " + type.getClass());
		}
		
		StringBuilder lockKey = new StringBuilder();
		ReadWriteKeyLockType rwType = (ReadWriteKeyLockType) type;
		
		switch(rwType) {
		case READ: lockKey.append(readKeyPrefix);
			break;
		case WRITE: lockKey.append(writeKeyPrefix);
			break;
		default:
			throw new IllegalArgumentException("Unknown type " + rwType);
				
		}
		
		// use a key strategy?
		return lockKey.append(separator).append(key).toString();
	}
	
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }
    /*
    private boolean acquireReadLock(String key) {
        boolean result = false;
        String wKey = getLockKey(key, ReadWriteKeyLockType.WRITE);
        String rKey = getLockKey(key, ReadWriteKeyLockType.READ);

		// Add a counter of some kind?
        while(!memcached.add(wKey, 60, 1)) { 
            sleep(1000);
        }

        memcache.incr(rKey, 1, 0);
        result = true;
        memcache.delete(wKey); // only want to delete if still mine
        return result;
    }
    
    public boolean releaseReadLock(Object key) {
        String rKey = getReadLockKey(key);
        memcache.decr(rKey, 1, 0);
        return true;
    }
    
    public boolean acquireWriteLock(Object key) {
        boolean result = false;
        String wKey = getWriteLockKey(key);
        String rKey = getReadLockKey(key);
        while (!memcache.add(wKey, 60, 1)) {
            sleep(1000);
        }
        result = true;
        while (((Integer) memcache.get(rKey)) > 0) {
            sleep(1000);
        }
        return result;
    }
    
    public boolean releaseWriteLock(Object key) {
        String wKey = getWriteLockKey(key);
        memcache.delete(wKey);
        return true;
    }
    */

}
