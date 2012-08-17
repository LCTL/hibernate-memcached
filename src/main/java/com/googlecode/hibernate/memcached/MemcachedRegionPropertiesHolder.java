package com.googlecode.hibernate.memcached;

import com.googlecode.hibernate.memcached.concurrent.keylock.ReadWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.strategy.clear.ClearStrategy;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;

public class MemcachedRegionPropertiesHolder {

    private String name;
    private String readLockKeyPrefix;
    private String writeLockKeyPrefix;
    private String clearIndexKeyPrefix;
    private String namespaceSeparator;
    private boolean clearSupported;
    private int cacheTimeSeconds;
    private KeyStrategy keyStrategy;
    private boolean dogpilePreventionEnabled;
    private int dogpilePreventionExpirationFactor;
    
    private ClearStrategy clearStrategy;
    private ReadWriteKeyLockProvider readWriteKeyLockProvider;
    
    public MemcachedRegionPropertiesHolder(String name, MemcachedRegionProperties properties) {
        this.name = name;
        
        this.readLockKeyPrefix = properties.getReadLockKeyPrefix(name);
        this.writeLockKeyPrefix = properties.getWriteLockKeyPrefix(name);
        this.clearIndexKeyPrefix = properties.getClearIndexKeyPrefix(name);
        this.namespaceSeparator = properties.getNamespaceSeparator(name);
        this.clearSupported = properties.isClearSupported(name);
        this.cacheTimeSeconds = properties.getCacheTimeSeconds(name);
        this.keyStrategy = properties.getKeyStrategy(name);
        this.dogpilePreventionEnabled = properties.isDogpilePreventionEnabled(name);
        this.dogpilePreventionExpirationFactor = properties.getDogpilePreventionExpirationFactor(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReadLockKeyPrefix() {
        return readLockKeyPrefix;
    }

    public void setReadLockKeyPrefix(String readLockKeyPrefix) {
        this.readLockKeyPrefix = readLockKeyPrefix;
    }

    public String getWriteLockKeyPrefix() {
        return writeLockKeyPrefix;
    }

    public void setWriteLockKeyPrefix(String writeLockKeyPrefix) {
        this.writeLockKeyPrefix = writeLockKeyPrefix;
    }

    public boolean isClearSupported() {
        return clearSupported;
    }

    public void setClearSupported(boolean clearSupported) {
        this.clearSupported = clearSupported;
    }

    public String getClearIndexKeyPrefix() {
        return clearIndexKeyPrefix;
    }

    public void setClearIndexKeyPrefix(String clearIndexKeyPrefix) {
        this.clearIndexKeyPrefix = clearIndexKeyPrefix;
    }

    public int getCacheTimeSeconds() {
        return cacheTimeSeconds;
    }

    public void setCacheTimeSeconds(int cacheTimeSeconds) {
        this.cacheTimeSeconds = cacheTimeSeconds;
    }

    public KeyStrategy getKeyStrategy() {
        return keyStrategy;
    }

    public void setKeyStrategy(KeyStrategy keyStrategy) {
         this.keyStrategy = keyStrategy;
    }

    public boolean isDogpilePreventionEnabled() {
        return dogpilePreventionEnabled;
    }

    public void setDogpilePreventionEnabled(boolean dogpilePreventionEnabled) {
        this.dogpilePreventionEnabled = dogpilePreventionEnabled;
    }

    public double getDogpilePreventionExpirationFactor() {
        return dogpilePreventionExpirationFactor;
    }

    public void setDogpilePreventionExpirationFactor(int dogpilePreventionExpirationFactor) {
        this.dogpilePreventionExpirationFactor = dogpilePreventionExpirationFactor;
    }

    public String getNamespaceSeparator() {
        return namespaceSeparator;
    }

    public void setNamespaceSeparator(String namespaceSeparator) {
        this.namespaceSeparator = namespaceSeparator;
    }

    public ClearStrategy getClearStrategy() {
        return clearStrategy;
    }

    public void setClearStrategy(ClearStrategy clearStrategy) {
        this.clearStrategy = clearStrategy;
    }

    public ReadWriteKeyLockProvider getReadWriteKeyLockProvider() {
        return readWriteKeyLockProvider;
    }

    public void setReadWriteKeyLockProvider(ReadWriteKeyLockProvider readWriteKeyLockProvider) {
        this.readWriteKeyLockProvider = readWriteKeyLockProvider;
    }

	@Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof MemcachedRegionPropertiesHolder)) { return false; }
        MemcachedRegionPropertiesHolder p = (MemcachedRegionPropertiesHolder) o;
        return name == p.getName() || (name != null && name.equals(p.getName()));
    }
    
    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }
}
