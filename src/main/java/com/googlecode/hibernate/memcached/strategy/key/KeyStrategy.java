package com.googlecode.hibernate.memcached.strategy.key;

/**
 * Interface for taking an <code>Object</code> and turning it into a key.
 * 
 * @author Ray Krueger
 */
public interface KeyStrategy {

    /**
     * Turns an <code>Object</code> into a key <code>String</code>.
     * 
     * @param o the <code>Object</code> to turn into a key
     * @return  a key <code>String</code>
     */
    String toKey(Object o);
    
}
