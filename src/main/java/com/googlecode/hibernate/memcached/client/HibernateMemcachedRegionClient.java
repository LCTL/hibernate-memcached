package com.googlecode.hibernate.memcached.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hibernate.cache.spi.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.MemcachedRegionProperties;
import com.googlecode.hibernate.memcached.MemcachedRegionSettings;
import com.googlecode.hibernate.memcached.strategy.key.encoding.KeyEncodingStrategy;
import com.googlecode.hibernate.memcached.utils.MemcachedRegionSettingsUtils;
import com.googlecode.hibernate.memcached.utils.StringUtils;


/**
 * A {@link HibernateMemcachedClient} for accessing keys in a particular region
 * at a particular clear index. This implementation is meant to be shared by any
 * clients that are expecting to operate on the same clear index.
 * <p>
 * <b>Keys:</b> Should be unique identifiers for the object to be placed in
 * the region this client supports. This implementation will add additional 
 * prefixing to a key using the {@link MemcachedRegionSettings#getName()} and
 * the clear index constructor argument. Additionally, all keys will be encoded
 * using a {@link MemcachedRegionSettingsUtils#getValidatedMemcachedKeyEncodingStrategy(MemcachedRegionSettings)}.
 * <p>
 * <b>Dogpile Prevention(stampeding herd prevention?):</b> Dogpile prevention
 * can be used to reduce the number of requests to the data store for an 
 * <code>Object</code> in the time between when an <code>Object</code> expires
 * from the cache and the time it has been added back into the cache (usually
 * after a db query).
 * <p>
 * It works by storing a dogpile token for each <code>Object</code> in the
 * cache for the requested <code>cacheTime</code> (usually 
 * {@link Region#getTimeout()}) and then storing the actual <code>Object</code>
 * for some longer amount of time. If enabled any <code>get</code> request will
 * retrieve both the dogpile token in addition to the requested 
 * <code>Object</code>. Once a request has found that the dogpile token for an
 * <code>Object</code> has expired it will reset the dogpile token (set to 
 * expire at {@link MemcachedRegionSettings#getCacheTimeSeconds()}) and return
 * <code>null</code> instead of the requested <code>Object</code>. This will
 * indicate to the client that it should update the cache with fresh data, 
 * while other requests will still get data from the cache while the loading
 * happens.
 * <p>
 * The amount of extra time an <code>Object</code> is stored in the cache is
 * configurable via the
 * {@link MemcachedRegionSettings#getDogpilePreventionExpirationFactor()}.
 * The dogpile prevention expiration factor is multiplied by the requested 
 * <code>cacheTime</code> to determine how long an <code>Object</code> should
 * be cached. As such the dogpile prevention expiration factor should be >1 in
 * order for dogpile prevention to work as intended.
 * <p>
 * This is, of course, only helpful if your data expires from the cache and you
 * expect many requests for an <code>Object</code> in the time that it takes to
 * refresh the cache after an expiration.
 * 
 * @see MemcachedRegionProperties
 */
public class HibernateMemcachedRegionClient implements HibernateMemcachedClient {

    private static final Logger log = LoggerFactory.getLogger(HibernateMemcachedRegionClient.class);
    
    private static final Integer DOGPILE_TOKEN = 0;

    private final HibernateMemcachedClient client;
    private final MemcachedRegionSettings settings;
    private final long clearIndex;
    
    private final String fullDogpileTokenKeyPrefix;
    private final KeyEncodingStrategy keyEncodingStrategy;
    
    private final String name;
    private final int cacheTime;
    
    public HibernateMemcachedRegionClient(HibernateMemcachedClient client, MemcachedRegionSettings settings, long clearIndex2) {
        this.client = client;
        this.settings = settings;
        this.clearIndex = clearIndex2;

        this.fullDogpileTokenKeyPrefix = 
                MemcachedRegionSettingsUtils.getFullDogpileTokenKeyPrefix(settings);
        this.keyEncodingStrategy = 
                MemcachedRegionSettingsUtils.getValidatedMemcachedKeyEncodingStrategy(settings);
        
        this.name = settings.getName();
        this.cacheTime = settings.getCacheTimeSeconds(); // used for dogpile timeout, make separate config?
    }
    
    @Override
    public Object get(String key) {
        String fullKey = getFullKey(key);
        
        if (settings.isDogpilePreventionEnabled()) {
            Map<String, Object> multi = getMultiUsingDogpilePrevention(key);
            return multi.get(fullKey);
        } else {
            log.debug("Memcached.get({})", fullKey);
            return client.get(fullKey);
        }
    }

    @Override
    public Map<String, Object> getMulti(String... keys) {
        if (settings.isDogpilePreventionEnabled()) {
            return getMultiUsingDogpilePrevention(keys);
        } else {
            keys = getFullKeyMulti(keys);
            log.debug("Memcached.getMulti({})", keys);
            return client.getMulti(keys);
        }
        
    }

    @Override
    public boolean set(String key, int cacheTimeSeconds, Object o) {
        int cacheTime = setDogpileKey(cacheTimeSeconds, key);
        String fullKey = getFullKey(key);
        log.debug("Memcached.set({})", fullKey);
        return client.set(fullKey, cacheTime, o);
    }
    
    @Override
    public boolean add(String key, int cacheTimeSeconds, Object o) {
        int cacheTime = setDogpileKey(cacheTimeSeconds, key);
        String fullKey = getFullKey(key);
        log.debug("Memcached.add({})", fullKey);
        return client.add(fullKey, cacheTime, o);
    }
    
    @Override
    public boolean delete(String key) {
        // delete dogpile token also?
        String fullKey = getFullKey(key);
        return client.delete(fullKey);
    }

    @Override
    public long incr(String key, long factor, long startingValue) {
        String fullKey = getFullKey(key);
        return client.incr(fullKey, factor, startingValue);
    }

    @Override
    public long decr(String key, long by, long startingValue) {
        String fullKey = getFullKey(key);
        return client.decr(fullKey, by, startingValue);
    }

    @Override
    public void shutdown() {
        client.shutdown();
    }

    // private and protected methods
    
    /**
     * Sets the dogpile token, if dogpile prevention is enabled.
     *
     * @param cacheTime  the amount of time to cache the dogpile token
     * @param key        the identifier for an object to be stored
     * @return           the amount of time to cache an {@link Object} under
     *                   the given key
     */
    private int setDogpileKey(int cacheTime, String key) {
        if (settings.isDogpilePreventionEnabled()) {
            String dogpileKey = getDogpileTokenKey(key);
            log.debug("Dogpile prevention enabled, setting token and adjusting object cache time. Key: [{}]", dogpileKey);
            client.set(dogpileKey, cacheTime, DOGPILE_TOKEN);
            cacheTime = (int) (cacheTime * settings.getDogpilePreventionExpirationFactor());
            // does this play nice with minimal puts?
        }
        
        return cacheTime;
    }
    
    /**
     * Gets all the objects in Memcached associated with the given keys using 
     * dogpile prevention rules.
     * <p>
     * This method has the side effect of setting the dogpile token for an
     * <code>Object</code> if the <code>Object</code> exists in the cache and 
     * its token has expired, though any such <code>Object</code> wont appear 
     * in the result <code>Map</code>.
     * 
     * @param keys       a list of identifiers to look up
     * @return           a {@link Map} of each key to its associated 
     *                   {@link Object} from the cache, if one exists and has
     *                   its dogpile token set. Or an empty {@link Map}
     */
    private Map<String, Object> getMultiUsingDogpilePrevention(String ... keys) {
        List<String> allKeys = new ArrayList<String>(keys.length*2);
        
        String[] fullKeys = getFullKeyMulti(keys);
        String[] dogpileKeys = getDogpileTokenKeyMulti(keys);
        log.debug("Checking dogpile keys: [{}]", dogpileKeys);
        
        allKeys.addAll(Arrays.asList(fullKeys));
        allKeys.addAll(Arrays.asList(dogpileKeys));
        
        log.debug("Memcache.getMulti({})", allKeys);
        Map<String, Object> multi = client.getMulti((String[]) allKeys.toArray());

        for (int i = 0; i < keys.length; i++) {
            if (multi.get(fullKeys[i]) != null && multi.get(dogpileKeys[i]) == null) {
                log.debug("Dogpile key ({}) not found updating token and returning null", dogpileKeys[i]);
                client.set(dogpileKeys[i], cacheTime, DOGPILE_TOKEN);
                multi.remove(fullKeys[i]);
            }
            multi.remove(dogpileKeys[i]);
        }

        return multi;
    }
    
    // Private Key Constructors
    
    private String getDogpileTokenKey(String key) {
        return getFullKey(fullDogpileTokenKeyPrefix, key);
    }
    
    private String[] getDogpileTokenKeyMulti(String ... keys) {
        return getFullKeyMulti(fullDogpileTokenKeyPrefix, keys);
    }
    
    private String getFullKey(String key) {
        return getFullKey(name, key);
    }
    
    private String[] getFullKeyMulti(String ... keys) {
        return getFullKeyMulti(name, keys);
    }
    
    private String getFullKey(String prefix, String key) {
        return keyEncodingStrategy.encode(concatenateKey(prefix, key));
    }
    
    private String[] getFullKeyMulti(String prefix, String ... keys) {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = keyEncodingStrategy.encode(concatenateKey(prefix,  keys[i]));
        }
        
        return keys;
    }
    
    private String concatenateKey(String prefix, String key) {
        return StringUtils.join(settings.getNamespaceSeparator(), prefix, clearIndex, key);
    }
}
