package com.googlecode.hibernate.memcached.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.hibernate.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.LoggingMemcacheExceptionHandler;

/**
 * @author Ray Krueger
 */
public class StringUtils {

    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        int arraySize = array.length;
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buffer.append(separator);
            }
            if (array[i] != null) {
                buffer.append(array[i]);
            }
        }
        return buffer.toString();
    }

    public static String md5Hex(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        byte[] bytes = digest("MD5", data);

        return toHexString(bytes);
    }

    public static String sha1Hex(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        byte[] bytes = digest("SHA1", data);

        return toHexString(bytes);
    }
    
    public static <T extends Object> T newInstance(String className, Object ... args) {
        T result = null;
        
        try {
        
            Class<T> clazz = (Class<T>) Class.forName(className);
        
            if (args == null) {
                result = clazz.newInstance();
            } else {
                Class<?>[] types = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    types[i] = args[i].getClass();
                }
            
                result = clazz.getConstructor(types).newInstance(args);
            }
        
        } catch (InstantiationException e) {
            log.error("Could not instantiate " + className + " class", e);
        } catch (IllegalAccessException e) {
            log.error("Could not instantiate " + className + " class", e);
        } catch (ClassNotFoundException e) {
            log.error("Could not instantiate " + className + " class", e);
        } catch (NoSuchMethodException e) {
            log.error("Could not instantiate " + className + " class", e);
        } catch (InvocationTargetException e) {
            log.error("Could not instantiate " + className + " class", e);
        }
        
        return result;
    }

    private static String toHexString(byte[] bytes) {
        int l = bytes.length;

        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & bytes[i]) >>> 4];
            out[j++] = DIGITS[0x0F & bytes[i]];
        }

        return new String(out);
    }

    private static byte[] digest(String algorithm, String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return digest.digest(data.getBytes());
    }
}
