package com.googlecode.hibernate.memcached.strategy.clear;

/**
 * An interface used to abstract the concept of clearing a portion of Memcached.
 * <p>
 * In order to support clearing for a set of <code>Object</code>s, all 
 * <code>Object</code>s in the set should share a clear index generation 
 * strategy and use the generated indices as a part of their key generation 
 * strategy. This will enable a logical clearing of data when a clear index
 * is changed because no requests using keys generated with old clear indices
 * will be made, thus allowing data stored under those keys to expire or be
 * ejected.
 */
public interface ClearStrategy {

    /**
     * Modifies the clear index so that calls to <code>getClearIndex</code>
     * return a new value.
     * 
     * @return <code>true</code> if the clearing was successful, 
     *         <code>false</code> otherwise
     */
    boolean clear();
    
    /**
     * Gets the current clear index.
     * 
     * @return the current clear index
     */
    long getClearIndex();
}
