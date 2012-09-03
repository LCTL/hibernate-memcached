package com.googlecode.hibernate.memcached.strategy.key.encoding;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encodes a key by sequentially applying the <code>encode</code> method of 
 * all {@link KeyEncodingStrategy}s added to this 
 * <code>KeyEncodingStrategy</code> (following {@link List#add(Object)}
 * semantics).
 */
public class NestedKeyEncodingStrategy implements KeyEncodingStrategy {

    private static final Logger log = LoggerFactory.getLogger(NestedKeyEncodingStrategy.class);

    private List<KeyEncodingStrategy> keyEncodingStrategies;
    
    public NestedKeyEncodingStrategy() {
        this.keyEncodingStrategies = new ArrayList<KeyEncodingStrategy>();
    }
    
    @Override
    public String encode(String key) {
        String result = key;
        for (KeyEncodingStrategy strategy : keyEncodingStrategies) {
            result = strategy.encode(result);
        }
        log.debug("nested encode({}) -> {}", key, result);
        return result;
    }
    
    /**
     * Adds any non <code>null</code> {@link KeyEncodingStrategy} to this 
     * <code>KeyEncodingStrategy</code> following {@link List#add(Object)}
     * semantics.
     * 
     * @param strategy a strategy to add
     * @return         <code>true</code> if the strategy was successfully added,
     *                 <code>false</code> otherwise
     */
    public boolean addStrategy(KeyEncodingStrategy strategy) {
        if (strategy != null) {
            return this.keyEncodingStrategies.add(strategy);
        }
        
        return false;
    }

}
