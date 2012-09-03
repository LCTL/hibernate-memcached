package com.googlecode.hibernate.memcached.strategy.key;

/**
 * Turns an <code>Object</code> into a key using {@link String#valueOf(Object)}.
 * 
 */
public class ToStringKeyStrategy implements KeyStrategy {

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if the argument is null
     */
    @Override
    public String toKey(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("null cannot be turned into a key.");
        }
        
        return String.valueOf(o);
    }

}
