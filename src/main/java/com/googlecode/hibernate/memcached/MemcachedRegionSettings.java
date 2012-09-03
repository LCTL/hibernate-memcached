package com.googlecode.hibernate.memcached;

import org.hibernate.cfg.Settings;

import com.googlecode.hibernate.memcached.concurrent.keylock.MemcachedReadWriteKeyLockProvider;
import com.googlecode.hibernate.memcached.strategy.clear.ClearStrategy;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;
import com.googlecode.hibernate.memcached.strategy.key.encoding.KeyEncodingStrategy;

/**
 * A class for storing region level configuration settings.
 *
 * @see MemcachedRegionProperties
 */
public class MemcachedRegionSettings {

    private Settings hibernateSettings; // Should these be stored also?

    private String name;
    private String readLockKeyPrefix;
    private String writeLockKeyPrefix;
    private String clearIndexKeyPrefix;
    private String namespaceSeparator;
    private boolean clearSupported;
    private int cacheTimeSeconds;
    private KeyStrategy keyStrategy;
    private KeyEncodingStrategy keyEncodingStrategy;
    private String dogpileTokenKeyPrefix;
    private boolean dogpilePreventionEnabled;
    private int dogpilePreventionExpirationFactor;
    
    private ClearStrategy clearStrategy;
    private MemcachedReadWriteKeyLockProvider readWriteKeyLockProvider;
    
    /**
     * A constructor that initializes as many settings as possible using the
     * given properties.
     * 
     * @param name       the name of the region these settings are for
     * @param properties properties used to initialize the settings
     */
    public MemcachedRegionSettings(String name, MemcachedRegionProperties properties) {
        this.name = name;
        
        this.readLockKeyPrefix = properties.getReadLockKeyPrefix(name);
        this.writeLockKeyPrefix = properties.getWriteLockKeyPrefix(name);
        this.clearIndexKeyPrefix = properties.getClearIndexKeyPrefix(name);
        this.dogpileTokenKeyPrefix = properties.getDogpileTokenKeyPrefix(name);
        this.namespaceSeparator = properties.getNamespaceSeparator(name);
        this.clearSupported = properties.isClearSupported(name);
        this.cacheTimeSeconds = properties.getCacheTimeSeconds(name);
        this.keyStrategy = properties.getKeyStrategy(name);
        this.keyEncodingStrategy = properties.getKeyEncodingStrategy(name);
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

    public KeyEncodingStrategy getKeyEncodingStrategy() {
		return keyEncodingStrategy;
	}

	public void setKeyEncodingStrategy(KeyEncodingStrategy keyEncodingStrategy) {
		this.keyEncodingStrategy = keyEncodingStrategy;
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

    public MemcachedReadWriteKeyLockProvider getReadWriteKeyLockProvider() {
        return readWriteKeyLockProvider;
    }

    public void setReadWriteKeyLockProvider(MemcachedReadWriteKeyLockProvider readWriteKeyLockProvider) {
        this.readWriteKeyLockProvider = readWriteKeyLockProvider;
    }

	public Settings getHibernateSettings() {
		return hibernateSettings;
	}

	public void setHibernateSettings(Settings hibernateSettings) {
		this.hibernateSettings = hibernateSettings;
	}

	public String getDogpileTokenKeyPrefix() {
		return dogpileTokenKeyPrefix;
	}

	public void setDogpileTokenKeyPrefix(String dogpileTokenKeyPrefix) {
		this.dogpileTokenKeyPrefix = dogpileTokenKeyPrefix;
	}
}
