package com.googlecode.hibernate.memcached.strategy.key.encoding;

/**
 * An encoding strategy that makes no change to the given key.
 */
public class NonEncodingKeyEncodingStrategy implements KeyEncodingStrategy {

    @Override
    public String encode(String key) {
        return key;
    }

}
