package com.googlecode.hibernate.memcached;

import java.lang.reflect.InvocationTargetException;

import org.hibernate.cache.CacheException;

import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;
import com.googlecode.hibernate.memcached.client.spymemcached.SpyMemcacheClientFactory;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;
import com.googlecode.hibernate.memcached.strategy.key.Sha1KeyStrategy;
import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * Configures an instance of {@link MemcachedCache} for use as a second-level cache in Hibernate.
 * To use set the hibernate property <i>hibernate.cache.provider_class</i> to the name of this class.
 * <p/>
 * There are two types of property settings that {@link MemcachedRegionFactory} supports, cache-wide properties
 * and region-name properties.
 * <p/>
 * <b>Cache wide properties</b>
 * <table border='1'>
 * <tr><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr><td>hibernate.memcached.servers</td><td>localhost:11211</td>
 * <td>Space delimited list of memcached instances in host:port format</td></tr>
 * <tr><td>hibernate.memcached.cacheTimeSeconds</td><td>300</td>
 * <td>The default number of seconds items should be cached. Can be overriden at the regon level.</td></tr>
 * <tr><td>hibernate.memcached.keyStrategy</td><td>{@link Sha1KeyStrategy}</td>
 * <td>Sets the strategy class to to use for generating cache keys.
 * Must provide a class name that implements {@link com.googlecode.hibernate.memcached.KeyStrategy}</td></tr>
 * <tr><td>hibernate.memcached.readBufferSize</td>
 * <td>{@link net.spy.memcached.DefaultConnectionFactory#DEFAULT_READ_BUFFER_SIZE}</td>
 * <td>The read buffer size for each server connection from this factory</td></tr>
 * <tr><td>hibernate.memcached.operationQueueLength</td>
 * <td>{@link net.spy.memcached.DefaultConnectionFactory#DEFAULT_OP_QUEUE_LEN}</td>
 * <td>Maximum length of the operation queue returned by this connection factory</td></tr>
 * <tr><td>hibernate.memcached.operationTimeout</td>
 * <td>{@link net.spy.memcached.DefaultConnectionFactory#DEFAULT_OPERATION_TIMEOUT}</td>
 * <td>Default operation timeout in milliseconds</td></tr>
 * <tr><td>hibernate.memcached.hashAlgorithm</td><td>{@link net.spy.memcached.HashAlgorithm#KETAMA_HASH}</td>
 * <td>Which hash algorithm to use when adding items to the cache.<br/>
 * <b>Note:</b> the MemcachedClient defaults to using
 * {@link net.spy.memcached.HashAlgorithm#NATIVE_HASH}, while the hibernate-memcached cache defaults to KETAMA_HASH
 * for "consistent hashing"</td></tr>
 * <tr><td>hibernate.memcached.clearSupported</td><td>false</td>
 * <td>Enables support for the {@link MemcachedCache#clear()} method for all cache regions.
 * The way clear is implemented for memcached is expensive and adds overhead to all get/set operations.
 * It is not recommended for production use.</td></tr>
 * </table>
 * <p/>
 * <b>Cache Region properties</b><br/>
 * Cache regon properties are set by giving your cached data a "region name" in hibernate.
 * You can tune the MemcachedCache instance for your region using the following properties.
 * These properties essentially override the cache-wide properties above.<br/>
 * <table border='1'>
 * <tr><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr><td>hibernate.memcached.[region-name].cacheTimeSeconds</td>
 * <td>none, see hibernate.memcached.cacheTimeSeconds</td>
 * <td>Set the cache time for this cache region, overriding the cache-wide setting.</td></tr>
 * <tr><td>hibernate.memcached.[region-name].keyStrategy</td><td>none, see hibernate.memcached.keyStrategy</td>
 * <td>Overrides the strategy class to to use for generating cache keys in this cache region.
 * Must provide a class name that implements {@link com.googlecode.hibernate.memcached.strategy.key.KeyStrategy}</td></tr>
 * <tr><td>hibernate.memcached.[region-name].clearSupported</td>
 * <td>none, see hibernate.memcached.clearSupported</td>
 * <td>Enables clear() operations for this cache region only.
 * Again, the clear operation incurs cost on every get/set operation.</td>
 * </tr>
 * </table>
 *
 * @author Ray Krueger
 */
public class Config {

    public static final String PROP_PREFIX = "hibernate.memcached.";

    private static final String CACHE_TIME_SECONDS = "cacheTimeSeconds";
    public static final String PROP_CACHE_TIME_SECONDS = PROP_PREFIX + CACHE_TIME_SECONDS;

    private static final String CLEAR_SUPPORTED = "clearSupported";
    public static final String PROP_CLEAR_SUPPORTED = PROP_PREFIX + CLEAR_SUPPORTED;

    private static final String MEMCACHE_CLIENT_FACTORY = "memcacheClientFactory";
    public static final String PROP_MEMCACHE_CLIENT_FACTORY = PROP_PREFIX + MEMCACHE_CLIENT_FACTORY;

    private static final String DOGPILE_PREVENTION = "dogpilePrevention";
    public static final String PROP_DOGPILE_PREVENTION = PROP_PREFIX + DOGPILE_PREVENTION;

    private static final String DOGPILE_PREVENTION_EXPIRATION_FACTOR = "dogpilePrevention.expirationFactor";
    public static final String PROP_DOGPILE_PREVENTION_EXPIRATION_FACTOR = PROP_PREFIX + DOGPILE_PREVENTION_EXPIRATION_FACTOR;

    private static final String KEY_STRATEGY = "keyStrategy";
    private static final String PROP_KEY_STRATEGY = PROP_PREFIX + KEY_STRATEGY;

    public static final int DEFAULT_CACHE_TIME_SECONDS = 300;
    public static final boolean DEFAULT_CLEAR_SUPPORTED = false;
    public static final boolean DEFAULT_DOGPILE_PREVENTION = false;
    public static final String DEFAULT_MEMCACHE_CLIENT_FACTORY = SpyMemcacheClientFactory.class.getName();
    public static final KeyStrategy DEFAULT_KEY_STRATEGY = new Sha1KeyStrategy();
    
    private PropertiesHelper props;
    private static final int DEFAULT_DOGPILE_EXPIRATION_FACTOR = 2;

    public Config(PropertiesHelper props) {
        this.props = props;
    }

    public int getCacheTimeSeconds(String cacheRegion) {
        int globalCacheTimeSeconds = props.getInt(PROP_CACHE_TIME_SECONDS,
                DEFAULT_CACHE_TIME_SECONDS);
        return props.getInt(cacheRegionPrefix(cacheRegion) + CACHE_TIME_SECONDS,
                globalCacheTimeSeconds);
    }

    public String getKeyStrategyName(String cacheRegion) {
        String globalKeyStrategy = props.get(PROP_KEY_STRATEGY,
                Sha1KeyStrategy.class.getName());
        return props.get(cacheRegionPrefix(cacheRegion) + KEY_STRATEGY, globalKeyStrategy);
    }
    
    public KeyStrategy getKeyStrategy(String cacheRegion) {
        String keyStrategyName = getKeyStrategyName(cacheRegion);
        return StringUtils.newInstance(keyStrategyName);
    }

    public boolean isClearSupported(String cacheRegion) {
        boolean globalClearSupported = props.getBoolean(PROP_CLEAR_SUPPORTED,
                DEFAULT_CLEAR_SUPPORTED);
        return props.getBoolean(cacheRegionPrefix(cacheRegion) + CLEAR_SUPPORTED,
                globalClearSupported);
    }

    public boolean isDogpilePreventionEnabled(String cacheRegion) {
        boolean globalDogpilePrevention = props.getBoolean(PROP_DOGPILE_PREVENTION,
                DEFAULT_DOGPILE_PREVENTION);
        return props.getBoolean(cacheRegionPrefix(cacheRegion) + DOGPILE_PREVENTION,
                globalDogpilePrevention);
    }

    public double getDogpilePreventionExpirationFactor(String cacheRegion) {
        double globalFactor = props.getDouble(PROP_DOGPILE_PREVENTION_EXPIRATION_FACTOR,
                DEFAULT_DOGPILE_EXPIRATION_FACTOR);
        return props.getDouble(cacheRegionPrefix(cacheRegion) + DOGPILE_PREVENTION_EXPIRATION_FACTOR,
                globalFactor);
    }

    public String getMemcachedClientFactoryName() {
        return props.get(PROP_MEMCACHE_CLIENT_FACTORY,
                DEFAULT_MEMCACHE_CLIENT_FACTORY);
    }
    
    public HibernateMemcachedClientFactory getMemcachedClientFactory() {
        String factoryClassName = getMemcachedClientFactoryName();
        return StringUtils.newInstance(factoryClassName, props);
    }

    private String cacheRegionPrefix(String cacheRegion) {
        return PROP_PREFIX + cacheRegion + ".";
    }

    public PropertiesHelper getPropertiesHelper() {
        return props;
    }
    
    
}
