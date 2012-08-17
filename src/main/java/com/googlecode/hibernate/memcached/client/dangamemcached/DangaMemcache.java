package com.googlecode.hibernate.memcached.client.dangamemcached;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.googlecode.hibernate.memcached.LoggingMemcacheExceptionHandler;
import com.googlecode.hibernate.memcached.MemcacheExceptionHandler;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * DOCUMENT ME!
 *
 * @author George Wei
 */
public class DangaMemcache implements HibernateMemcachedClient {

    private static final Logger log = LoggerFactory.getLogger(DangaMemcache.class);

    private final MemCachedClient memcachedClient;
    private final String poolName;

    private MemcacheExceptionHandler exceptionHandler = new LoggingMemcacheExceptionHandler();

    /* Constructor
     *
     * @param memcachedClient Instance of Danga's MemCachedClient
     * @param poolName SockIOPool name used to instantiate memcachedClient
     */
    public DangaMemcache(MemCachedClient memcachedClient, String poolName) {
        this.memcachedClient = memcachedClient;
        this.poolName = poolName;
    }

    public Object get(String key) {
        try {
            log.debug("MemCachedClient.get({})", key);
            return memcachedClient.get(key);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnGet(key, e);
        }
        return null;
    }

    public Map<String, Object> getMulti(String... keys) {
        try {
            return memcachedClient.getMulti(keys);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnGet(StringUtils.join(keys, ", "), e);
        }
        return null;
    }

    public boolean set(String key, int cacheTimeSeconds, Object o) {
        log.debug("MemCachedClient.set({})", key);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.SECOND, cacheTimeSeconds);
            return memcachedClient.set(key, o, calendar.getTime());
        } catch (Exception e) {
            exceptionHandler.handleErrorOnSet(key, cacheTimeSeconds, o, e);
        }
        return false;
    }

    public void delete(String key) {
        try {
            memcachedClient.delete(key);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnDelete(key, e);
        }
    }

    public long incr(String key, long factor, long startingValue) {
        long rv = -1;
        
        try {
            //Try to incr
            rv = memcachedClient.incr(key, factor);

            //If the key is not found, add it with startingValue
            if (-1 == rv)
                rv = memcachedClient.addOrIncr(key, startingValue);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnIncr(key, factor, startingValue, e);
        }
        
        return rv;
    }

    public void shutdown() {
        log.debug("Shutting down danga MemCachedClient");

        //Danga's MemCachedClient does not provide a method to shutdown or
        //close it, let's shutdown its SockIOPool instead
        SockIOPool.getInstance(poolName).shutDown();
    }

    public void setExceptionHandler(MemcacheExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

	@Override
	public boolean add(String key, int exp, Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long decr(String key, long by, long startingValue) {
		// TODO Auto-generated method stub
		return 0;
	}
}
