package com.googlecode.hibernate.memcached.strategy.key.encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * Encodes keys using the Sha1 hashing algorithm.
 */
public class Sha1KeyEncodingStrategy implements KeyEncodingStrategy {

    private static final Logger log = LoggerFactory.getLogger(Sha1KeyEncodingStrategy.class);
    
    @Override
    public String encode(String key) {
        String result = StringUtils.sha1Hex(key);
        log.debug("encode({}) -> {}", key, result);
        return result;
    }

}
