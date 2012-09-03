package com.googlecode.hibernate.memcached.strategy.key.encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.hibernate.memcached.utils.StringUtils;

/**
 * Encodes keys using the MD5 hashing algorithm.
 */
public class Md5KeyEncodingStrategy implements KeyEncodingStrategy {

    private static final Logger log = LoggerFactory.getLogger(Md5KeyEncodingStrategy.class);
    @Override
    public String encode(String key) {
        String result = StringUtils.md5Hex(key);
        log.debug("encode({}) -> {}", key, result);
        return result;
    }


}
