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
 * A utility class for {@link String}s.
 * 
 * @author Ray Krueger
 */
public class StringUtils {

    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * Joins a collection of objects together as a <code>String</code> delimited
     * by the given separator. <code>Object</code>s are converted to 
     * <code>String</code>s using {@link String#valueOf(Object)}.
     * 
     * @param separator the <code>String</code> used to delimit the given
     *                  <code>Object</code>s
     * @param objects   the <code>Object</code>s to be used in constructing the
     *                  <code>String</code>
     * @return          a <code>String</code> of the form: 
     *                  [obj1][separator][obj2][separator]...[separator][objN]
     */
    public static String join(String separator, Object ... objects) {
        if (objects == null) {
            return "";
        }
        
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            if (i > 0) {
                buffer.append(separator);
            }
            if (objects[i] != null) {
                buffer.append(objects[i]);
            }
        }
        return buffer.toString();
    }
    
    /**
     * Joins a collection of <code>String</code>s together delimited by the
     * given separator.
     * 
     * @param separator the <code>String</code> to be used as a delimiter
     * @param strings   the <code>String</code>s to be joined
     * @return          a <code>String</code> of the form: 
     *                  [str1][separator][str2][separator]...[separator][strN]
     */
    public static String join(String separator, String ... strings) {
        return join(separator, (Object[]) strings);
    }

    /**
     * Encodes a <code>String</code> using the MD5 hashing algorithm.
     * 
     * @param data the <code>String</code> to encode
     * @return     the hashed data expressed as a hex <code>String</code>
     */
    public static String md5Hex(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        byte[] bytes = digest("MD5", data);

        return toHexString(bytes);
    }

    /**
     * Encodes a <code>String</code> using the SHA1 hashing algorithm.
     * 
     * @param data the <code>String</code> to encode
     * @return     the hashed data expressed as a hex <code>String</code>
     */
    public static String sha1Hex(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        byte[] bytes = digest("SHA1", data);

        return toHexString(bytes);
    }
    
    /**
     * Encodes a <code>String</code> using the SHA256 hashing algorithm.
     * 
     * @param data the <code>String</code> to encode
     * @return     the hashed data expressed as a hex <code>String</code>
     */
    public static String sha256Hex(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        byte[] bytes = digest("SHA256", data);

        return toHexString(bytes);
    }
    
    /**
     * Creates a new instance of a class with the given name, instantiated with
     * the given arguments.
     * <br>
     * TODO: Improve constructor lookup?
     * 
     * @param className the fully qualified name of the desired class
     * @param args      the arguments used when instantiating the
     *                  <code>Object</code>
     * @return          a new <code>Object</code> of the desired class,
     *                  or <code>null</code>
     */
    public static <T extends Object> T newInstance(String className, Object ... args) {
        T result = null;
        
        try {
        
            Class<T> clazz = (Class<T>) Class.forName(className);
        
            if (args == null || args.length == 0) {
                result = clazz.newInstance();
            } else {
                Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
                
                for (int i = 0; result == null && i < constructors.length; i++) {
                    Class<?>[] constructorParamTypes = constructors[i].getParameterTypes();
                    boolean isMatch = args.length == constructorParamTypes.length;
                    for (int j = 0; isMatch && j < constructorParamTypes.length; j++) {
                        // is this always the best match?
                        isMatch = isMatch &&  constructorParamTypes[j].isInstance(args[j]);
                    }
                    
                    if (isMatch) {
                        result = constructors[i].newInstance(args);
                    }
                }
            }
        
        } catch (InstantiationException e) {
            log.error("Could not instantiate " + className + " class", e);
        } catch (IllegalAccessException e) {
            log.error("Could not instantiate " + className + " class", e);
        } catch (ClassNotFoundException e) {
            log.error("Could not instantiate " + className + " class", e);
        } catch (InvocationTargetException e) {
            log.error("Could not instantiate " + className + " class", e);
        }
        
        return result;
    }

    /**
     * Converts a <code>byte[]</code> into a hex <code>String</code>.
     * 
     * @param bytes the bytes to turn into a hex <code>String</code>
     * @return      a hex <code>String</code>
     */
    private static String toHexString(byte[] bytes) {
        int l = bytes.length;

        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & bytes[i]) >>> 4];
            out[j++] = DIGITS[0x0F & bytes[i]];
        }

        return new String(out);
    }

    /**
     * Converts a <code>String</code> into a <code>byte[]</code> using the
     * given algorithm.
     * 
     * @param algorithm the name of the digest algorithm
     * @param data      the data to convert
     * @return          the resulting <code>byte[]</code>
     * @see             MessageDigest
     */
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
