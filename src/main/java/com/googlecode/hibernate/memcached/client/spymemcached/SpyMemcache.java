package com.googlecode.hibernate.memcached.client.spymemcached;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.LoggingMemcacheExceptionHandler;
import com.googlecode.hibernate.memcached.MemcacheExceptionHandler;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * DOCUMENT ME!
 *
 * @author Ray Krueger
 */
public class SpyMemcache implements HibernateMemcachedClient {

    private static final Logger log = LoggerFactory.getLogger(SpyMemcache.class);
    private MemcacheExceptionHandler exceptionHandler = new LoggingMemcacheExceptionHandler();

    private final MemcachedClient memcachedClient;

    public SpyMemcache(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    public Object get(String key) {
        try {
            log.debug("MemcachedClient.get({})", key);
            return memcachedClient.get(key);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnGet(key, e);
        }
        return null;
    }

    public Map<String, Object> getMulti(String... keys) {
        Map<String, Object> result = null;
        try {
            result = memcachedClient.getBulk(keys);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnGet(StringUtils.join(", ", keys), e);
        }
        return result == null ? new HashMap<String,Object>(0) : result;
    }

    public boolean set(String key, int cacheTimeSeconds, Object o) {
        log.debug("MemcachedClient.set({})", key);
        try {
            memcachedClient.set(key, cacheTimeSeconds, o);
            return true;
        } catch (Exception e) {
            exceptionHandler.handleErrorOnSet(key, cacheTimeSeconds, o, e);
        }
        return false;
    }
    
    // TODO: implement correctly
    public boolean add(String key, int exp, Object o) {
        try {
            return memcachedClient.add(key, exp, o).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public boolean delete(String key) {
        try {
            return memcachedClient.delete(key).get();
        } catch (Exception e) {
            exceptionHandler.handleErrorOnDelete(key, e);
        }
        return false;
    }

    public long incr(String key, long factor, long startingValue) {
        try {
            return memcachedClient.incr(key, factor, startingValue);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnIncr(key, factor, startingValue, e);
        }
        
        return -1;
    }
    
    public long decr(String key, long by, long startingValue) {
        try {
            return memcachedClient.decr(key, by, startingValue);
        } catch (Exception e) {
            exceptionHandler.handleErrorOnIncr(key, by, startingValue, e);
        }
        return -1;
    }

    public void shutdown() {
        log.debug("Shutting down spy MemcachedClient");
        memcachedClient.shutdown();
    }

    public void setExceptionHandler(MemcacheExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }
}
