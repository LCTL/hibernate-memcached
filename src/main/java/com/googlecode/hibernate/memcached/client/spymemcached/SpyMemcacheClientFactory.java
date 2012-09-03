package com.googlecode.hibernate.memcached.client.spymemcached;

import java.io.IOException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.KetamaConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;

import org.hibernate.cache.CacheException;

import com.googlecode.hibernate.memcached.MemcachedProperties;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;

/**
 * A {@link HibernateMemcachedClientFactory} that creates 
 * {@link HibernateMemcachedClient}s backed by the SpyMemcached client.
 * <p>
 * Clients created by this class are configured with 
 * {@link SpyMemcachedProperties}.
 *
 * @author Ray Krueger
 * 
 * @see SpyMemcache
 * @see net.spy.memcached.MemcachedClient
 */
public class SpyMemcacheClientFactory implements HibernateMemcachedClientFactory {

    private final SpyMemcachedProperties properties;

    public SpyMemcacheClientFactory(MemcachedProperties properties) {
        this.properties = new SpyMemcachedProperties(properties);
    }

    @Override
    public HibernateMemcachedClient createMemcacheClient() {
        MemcachedClient client;
        
        try {
            ConnectionFactory connectionFactory = getConnectionFactory();
            client = new MemcachedClient(connectionFactory, AddrUtil.getAddresses(properties.getServerList()));
        } catch (IOException e) {
            throw new CacheException("Could not create cache client", e);
        }
        
        return new SpyMemcache(client);
    }

    protected ConnectionFactory getConnectionFactory() {

        if (connectionFactoryNameEquals(DefaultConnectionFactory.class)) {
            return buildDefaultConnectionFactory();
        }

        if (connectionFactoryNameEquals(KetamaConnectionFactory.class)) {
            return buildKetamaConnectionFactory();
        }

        if (connectionFactoryNameEquals(BinaryConnectionFactory.class)) {
            return buildBinaryConnectionFactory();
        }

        throw new IllegalArgumentException("Unsupported connection factory: " + properties.getConnectionFactoryName());
    }

    private boolean connectionFactoryNameEquals(Class<?> cls) {
        return cls.getSimpleName().equals(properties.getConnectionFactoryName());
    }

    private DefaultConnectionFactory buildDefaultConnectionFactory() {
        return new DefaultConnectionFactory(
                properties.getOperationQueueLength(), properties.getReadBufferSize(), properties.getHashAlgorithm()) {
            @Override
            public long getOperationTimeout() {
                return properties.getOperationTimeoutMillis();
            }

            @Override
            public boolean isDaemon() {
                return properties.isDaemonMode();
            }

            @Override
            public AuthDescriptor getAuthDescriptor() {
                return createAuthDescriptor();
            }
        };
    }

    private KetamaConnectionFactory buildKetamaConnectionFactory() {
        return new KetamaConnectionFactory() { // Doesn't configure properties.getOperationQueueLength(), properties.getReadBufferSize()
            @Override
            public long getOperationTimeout() {
                return properties.getOperationTimeoutMillis();
            }

            @Override
            public boolean isDaemon() {
                return properties.isDaemonMode();
            }

            @Override
            public AuthDescriptor getAuthDescriptor() {
                return createAuthDescriptor();
            }
        };
    }

    private BinaryConnectionFactory buildBinaryConnectionFactory() {
        return new BinaryConnectionFactory(
                properties.getOperationQueueLength(), properties.getReadBufferSize(), properties.getHashAlgorithm()) {
            @Override
            public long getOperationTimeout() {
                return properties.getOperationTimeoutMillis();
            }

            @Override
            public boolean isDaemon() {
                return properties.isDaemonMode();
            }

            @Override
            public AuthDescriptor getAuthDescriptor() {
                return createAuthDescriptor();
            }
        };
    }

    protected AuthDescriptor createAuthDescriptor() {
        String username = properties.getUsername();
        String password = properties.getPassword();
        if (username == null || password == null) {
            return null;
        }
        return new AuthDescriptor(new String[] { "PLAIN" },
                new PlainCallbackHandler(username, password));
    }
    
}
