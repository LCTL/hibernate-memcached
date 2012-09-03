package com.googlecode.hibernate.memcached.strategy.key.encoding;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for transforming a <code>String</code> into a valid Memcached key by
 * removing any illegal characters.
 * <p>
 * TODO: add control character removal
 * {@link https://github.com/memcached/memcached/blob/master/doc/protocol.txt}
 */
public class ValidMemcachedKeyEncodingStrategy implements KeyEncodingStrategy {

    private static final Logger log = LoggerFactory.getLogger(ValidMemcachedKeyEncodingStrategy.class);
    
    private static final Pattern CLEAN_PATTERN = Pattern.compile("\\s");
    private static final int MAX_KEY_LENGTH = 250;
    
    @Override
    public String encode(String key) {
        String result = CLEAN_PATTERN.matcher(key).replaceAll("");
        
        if (result.length() > MAX_KEY_LENGTH) {
            throw new IllegalArgumentException(String.format(
                    "Key(%s) could not be encoded into a valid Memcached key, try using the Sha1KeyEncodingStrategy: ",
                    result));
        }
        
        log.debug("encode({}) -> {}", key, result);
        return result;
    }

}
