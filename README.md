# Hibernate-memcached

A library for using Memcached as a [second level cache][1] in Hibernate.

  * Compatible with [Hibernate 4.1.5.SP1][2]
  
## Supported Clients

  * [spymemcached 2.8.1][3] (the default client)
  * [danga (Whalin) 2.0.1][4] (not provided)
  

## Notes

  * Supports entity and query caching
  * Does not support Natural keys or the Transactional CacheConcurrencyStrategy

## Usage

To [enable][1] caching in your application you must [configure][5] Hibernate

  * Example config:  

    ``<property name="hibernate.cache.provider_class">com.googlecode.hibernate.memcached.MemcachedRegionFactory</property>``  
    ``<property name="hibernate.cache.use_second_level_cache">true</property>``  
    ``<property name="hibernate.cache.use_query_cache">true</property>``  
	
To enable caching of a particular entity use Hibernates [@Cache][6] annotation (or the [*cache* mapping element][7]).

To [enable][8] caching of a particular query make sure it is cacheable [``org.hibernate.Query.setCacheable(true)``][9].

Additionaly, this implemenation supports three types of properties, cache-wide properties (See MemcachedProperties),
region-wide properties (See MemcachedRegionProperties), and client-wide properties (See SpyMemcachedProperties or DangaMemcacheClientFactory)

  * Example config:
  
    ``<property name="hibernate.memcached.memcacheClientFactory">com.googlecode.hibernate.memcached.client.spymemcached.SpyMemcacheClientFactory</property>``  
    ``<property name="hibernate.memcached.cacheTimeSeconds">300</property>``  
    ``<property name="hibernate.memcached.clearSupported">fase</property>``  
    ``<property name="hibernate.memcached.dogpilePrevention">false</property>``  
    ``<property name="hibernate.memcached.keyStrategy">com.googlecode.hibernate.memcached.strategy.key.ToStringKeyStrategy</property>``  
    ``<property name="hibernate.memcached.keyEncodingStrategy">com.googlecode.hibernate.memcached.strategy.key.encoding.Sha1KeyEncodingStrategy</property>``  
    ``<property name="hibernate.memcached.[region-name].cacheTimeSeconds">500</property>``  
    ``<property name="hibernate.memcached.servers">localhost:11211</property>``    

## Help

If you have any questions, or just want to drop a line to say it's working great :) use the [google-group][10].

Please note that this is an open source project. I work on it when I can and I 
implement what I feel like. I am volunteering my own free time for my own amusement.

## Patches/Pull Request FAQ

  * Fork the project.
  * Make your feature addition or bug fix.
  * Add tests for it. This is important so I don't break it in a future version unintentionally.
  * Commit, do not mess with pom.xml, version, or history. (if you want to have your own version, 
    that is fine but bump version in a commit by itself I can ignore when I pull)
  * Send me a pull request. Bonus points for topic branches.

[1]:  http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch20.html#performance-cache
[2]:  http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/
[3]:  https://code.google.com/p/spymemcached/
[4]:  https://github.com/gwhalin/Memcached-Java-Client/wiki
[5]:  http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch03.html#configuration-cache-properties
[6]:  http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch20.html#example-cache-annotation-with-attributes
[7]:  http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch20.html#example-hibernate-cache-mapping-element
[8]:  http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch20.html#performance-querycache-enable
[9]:  http://docs.jboss.org/hibernate/orm/4.1/manual/en-US/html/ch20.html#performance-querycache-regions
[10]: http://groups.google.com/group/hibernate-memcached