package com.googlecode.hibernate.memcached.strategy.key.encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes keys using their <code>hashCode</code> method.
 */
public class HashCodeKeyEncodingStrategy implements KeyEncodingStrategy {

    private static final Logger log = LoggerFactory.getLogger(HashCodeKeyEncodingStrategy.class);
    
    @Override
    public String encode(String key) {
        String result = String.valueOf(key.hashCode());
        log.debug("encode({}) -> {}", key, result);
        return result;
    }


}
