package com.googlecode.hibernate.memcached.strategy.key.encoding;

/**
 * An interface for key encoding.
 */
public interface KeyEncodingStrategy {

    /**
     * Encodes a key.
     * 
     * @param key the key to encode
     * @return    an encoded key
     */
    public String encode(String key);
    
}
