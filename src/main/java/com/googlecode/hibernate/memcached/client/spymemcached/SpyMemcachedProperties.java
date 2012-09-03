package com.googlecode.hibernate.memcached.client.spymemcached;

import java.util.Properties;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.HashAlgorithm;

import com.googlecode.hibernate.memcached.MemcachedProperties;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClient;
import com.googlecode.hibernate.memcached.client.HibernateMemcachedClientFactory;
import com.googlecode.hibernate.memcached.strategy.key.KeyStrategy;
import com.googlecode.hibernate.memcached.strategy.key.encoding.KeyEncodingStrategy;

/**
 * A class for reading client properties.
 * <p/>
 * <b>Client properties</b>
 * <table border='1'>
 * <tr><th>Property</th><th>Default</th><th>Description</th></tr>
 * <tr>
 * <td>hibernate.memcached.servers</td>
 * <td>localhost:11211</td>
 * <td>A string containing whitespace or comma separated host or IP addresses and port numbers of the form "host:port host2:port" or "host:port,host2:port".</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.operationQueueLength</td>
 * <td>{@link DefaultConnectionFactory.DEFAULT_OP_QUEUE_LEN}</td>
 * <td>The maximum length of the operation queue.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.readBufferSize</td>
 * <td>{@link DefaultConnectionFactory.DEFAULT_READ_BUFFER_SIZE}</td>
 * <td>The read buffer size for each server connection.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.operationTimeout</td>
 * <td>{@link DefaultConnectionFactory.DEFAULT_OPERATION_TIMEOUT}</td>
 * <td>Default operation timeout in milliseconds.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.daemonMode</td>
 * <td>false</td>
 * <td>If true, the IO thread should be a daemon thread.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.hashAlgorithm</td>
 * <td>KETAMA_HASH</td>
 * <td>The algorithm to use for hashing. <b>Note:</b> Must be a {@link DefaultHashAlgorithm}.</td>
 * </tr>
 * <td>hibernate.memcached.connectionFactory</td>
 * <td>{@link DefaultConnectionFactory}</td>
 * <td>The {@link ConnectionFactory} to be used by the client. <b>Supported
 * Values:</b> {@link DefaultConnectionFactory},
 * {@link KetamaConnectionFactory}, and {@link BinaryConnectionFactory}.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.username</td>
 * <td><code>null</code></td>
 * <td>A Memcached username.</td>
 * </tr>
 * <tr>
 * <td>hibernate.memcached.password</td>
 * <td><code>null</code></td>
 * <td>The users password.</td>
 * </tr>
 * </table>
 */
public class SpyMemcachedProperties extends MemcachedProperties {

    private static final long serialVersionUID = 1L;
    
    // Keys
    protected static final String SERVERS = "servers";
    protected static final String OPERATION_QUEUE_LENGTH = "operationQueueLength";
    protected static final String READ_BUFFER_SIZE = "readBufferSize";
    protected static final String OPERATION_TIMEOUT = "operationTimeout";
    protected static final String DAEMON_MODE = "daemonMode";
    protected static final String HASH_ALGORITHM = "hashAlgorithm";
    protected static final String CONNECTION_FACTORY = "connectionFactory";
    protected static final String USERNAME = "username";
    protected static final String PASSWORD = "password";

    // Defaults
    protected static final String DEFAULT_SERVERS = "localhost:11211";
    protected static final int DEFAULT_OPERATION_QUEUE_LENGTH = DefaultConnectionFactory.DEFAULT_OP_QUEUE_LEN;
    protected static final int DEFAULT_READ_BUFFER_SIZE = DefaultConnectionFactory.DEFAULT_READ_BUFFER_SIZE;
    protected static final long DEFAULT_OPERATION_TIMEOUT = DefaultConnectionFactory.DEFAULT_OPERATION_TIMEOUT;
    protected static final boolean DEFAULT_DAEMON_MODE = false;
    protected static final DefaultHashAlgorithm DEFAULT_HASH_ALGORITHM = DefaultHashAlgorithm.KETAMA_HASH;
    protected static final String DEFAULT_CONNECTION_FACTORY_NAME = DefaultConnectionFactory.class.getSimpleName();
    protected static final String DEFAULT_USERNAME = null;
    protected static final String DEFAULT_PASSWORD = null;
    
    public SpyMemcachedProperties(Properties properties) {
        super(properties);
    }

    public String getServerList() {
        String key = toKey(SERVERS);
        return get(key, DEFAULT_SERVERS);
    }

    public int getOperationQueueLength() {
        String key = toKey(OPERATION_QUEUE_LENGTH);
        return getInt(key, DEFAULT_OPERATION_QUEUE_LENGTH);
    }

    public int getReadBufferSize() {
        String key = toKey(READ_BUFFER_SIZE);
        return getInt(key, DEFAULT_READ_BUFFER_SIZE);
    }

    public long getOperationTimeoutMillis() {
        String key = toKey(OPERATION_TIMEOUT);
        return getLong(key, DEFAULT_OPERATION_TIMEOUT);
    }

    public boolean isDaemonMode() {
        String key = toKey(HASH_ALGORITHM);
        return getBoolean(key, DEFAULT_DAEMON_MODE);
    }

    public HashAlgorithm getHashAlgorithm() {
        String key = toKey(HASH_ALGORITHM);
        return getEnum(key, DefaultHashAlgorithm.class, DEFAULT_HASH_ALGORITHM);
    }

    public String getConnectionFactoryName() {
        String key = toKey(CONNECTION_FACTORY);
        return get(key, DEFAULT_CONNECTION_FACTORY_NAME);
    }
    
    public String getUsername() {
        String key = toKey(USERNAME);
        return get(key, DEFAULT_USERNAME);
    }
    
    public String getPassword() {
        String key = toKey(PASSWORD);
        return get(key, DEFAULT_PASSWORD);
    }
}
