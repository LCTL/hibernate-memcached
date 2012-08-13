package com.googlecode.hibernate.memcached.utils;

import java.util.Properties;

public class PropertiesUtils {

    public static String get(Properties props, String key) {
        return props.getProperty(key);
    }

    public static String get(Properties props, String key, String defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : val;
    }

    public static String findValue(Properties props,  String... keys) {
        for (String key : keys) {
            String value = props.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static boolean getBoolean(Properties props, String key, boolean defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Boolean.parseBoolean(val);
    }

    public static long getLong(Properties props, String key, long defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Long.parseLong(val);
    }

    public static int getInt(Properties props, String key, int defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Integer.parseInt(val);
    }

    public static double getDouble(Properties props, String key, double defaultVal) {
        String val = props.getProperty(key);
        return val == null ? defaultVal : Double.parseDouble(val);
    }

    public static <T extends Enum<T>> T getEnum(Properties props, String key, Class<T> type, T defaultValue) {
        String val = props.getProperty(key);
        return val == null ? defaultValue : Enum.valueOf(type, val);
    }
    
    public static <T extends Object> T getObject(Properties props, String key, T defaultValue, Object ... args) {
        String className = props.getProperty(key);
        
        if (className == null) {
            return defaultValue;
        }

        return StringUtils.newInstance(className, args);
    }
}
