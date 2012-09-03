package com.googlecode.hibernate.memcached.utils;

import java.util.Properties;

import com.googlecode.hibernate.memcached.MemcachedProperties;
import com.googlecode.hibernate.memcached.MemcachedRegionProperties;

/**
 * A utility class for {@link Properties}.
 * 
 * @see Properties
 * @see MemcachedProperties
 * @see MemcachedRegionProperties
 */
public class PropertiesUtils {

    /**
     * Gets the property associated with the given key.
     * 
     * @param props the {@link Properties} to search
     * @param key   the key to lookup
     * @return      the value associated with the given key,
     *              or <code>null</code>
     * @see         Properties#getProperty(String)
     */
    public static String get(Properties props, String key) {
        return props.getProperty(key);
    }

    /**
     * Gets the property associated with the given key. The default
     * value is returned if no property is associated with the given key.
     * 
     * @param props      the {@link Properties} to search
     * @param key        the key to lookup
     * @param defaultVal the default value
     * @return           the value associated with the given key,
     *                   or the <code>defaultValue</code>
     * @see              Properties#getProperty(String, String)
     */
    public static String get(Properties props, String key, String defaultVal) {
        String val = props.getProperty(key, defaultVal);
        return val;
    }

    /**
     * Gets the <code>boolean</code> property associated with the given key.
     * The default value is returned if no property is associated with the given
     * key.
     * 
     * @param props      the {@link Properties} to search
     * @param key        the key to lookup
     * @param defaultVal the default value
     * @return           the value associated with the given key,
     *                   or the <code>defaultValue</code>
     */
    public static boolean getBoolean(Properties props, String key, boolean defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Boolean.parseBoolean(val);
    }

    /**
     * Gets the <code>long</code> property associated with the given key.
     * The default value is returned if no property is associated with the given
     * key.
     * 
     * @param props      the {@link Properties} to search
     * @param key        the key to lookup
     * @param defaultVal the default value
     * @return           the value associated with the given key,
     *                   or the <code>defaultValue</code>
     */
    public static long getLong(Properties props, String key, long defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Long.parseLong(val);
    }

    /**
     * Gets the <code>int</code> property associated with the given key.
     * The default value is returned if no property is associated with the given
     * key.
     * 
     * @param props      the {@link Properties} to search
     * @param key        the key to lookup
     * @param defaultVal the default value
     * @return           the value associated with the given key,
     *                   or the <code>defaultValue</code>
     */
    public static int getInt(Properties props, String key, int defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Integer.parseInt(val);
    }

    /**
     * Gets the <code>double</code> property associated with the given key.
     * The default value is returned if no property is associated with the given
     * key.
     * 
     * @param props      the {@link Properties} to search
     * @param key        the key to lookup
     * @param defaultVal the default value
     * @return           the value associated with the given key,
     *                   or the <code>defaultValue</code>
     */
    public static double getDouble(Properties props, String key, double defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Double.parseDouble(val);
    }

    /**
     * Gets the {@link Enum} property associated with the given key.
     * The default value is returned if no property is associated with the given
     * key.
     * 
     * @param props      the {@link Properties} to search
     * @param key        the key to lookup
     * @param type       the <code>Class</code> of the expected enum
     * @param defaultVal the default value
     * @return           the value associated with the given key,
     *                   or the <code>defaultValue</code>
     * @see              Enum#valueOf(Class, String)
     */
    public static <T extends Enum<T>> T getEnum(Properties props, String key, Class<T> type, T defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Enum.valueOf(type, val);
    }
    
    /**
     * Gets the <code>Object</code> property associated with the given key
     * instantiated with the given arguments. The default value is returned if
     * no property is associated with the given key.
     * 
     * @param props      the {@link Properties} to search
     * @param key        the key to lookup
     * @param defaultVal the default value
     * @param args       the arguments used to instantiate the <code>Object</code>
     * @return           the value associated with the given key,
     *                   or the <code>defaultValue</code>
     * @see              StringUtils#newInstance(String, Object...)
     */
    public static <T extends Object> T getObject(Properties props, String key, T defaultVal, Object ... args) {
        String className = props.getProperty(key);
        
        if (className == null) {
            return defaultVal;
        }

        return StringUtils.newInstance(className, args);
    }
}
