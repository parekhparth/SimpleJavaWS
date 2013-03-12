package com.parthparekh.service;

import com.couchbase.client.CouchbaseClientIF;
import com.parthparekh.service.cache.*;
import net.sf.ehcache.CacheManager;
import net.spy.memcached.MemcachedClientIF;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Configuration class
 *
 * @author: Parth Parekh
 **/
@Configuration
public class ServiceConfiguration {
    @Value("${cache.type:memcached}")
    private String cacheType;

    @Value("${couchbase.cache.connection.uri}")
    private String couchbaseCacheConnectionURI;

    @Value("${memcached.cache.connection.uri}")
    private String memcachedCacheConnectionURI;

    @Value("${cache.read.timeout:500}")
    private int cacheReadTimeout;

    @Value("${cache.write.timeout:500}")
    private int cacheWriteTimeout;

    @Value("${cache.enqueue.timeout:500}")
    private int cacheEnqueueTimeout;

    private ObjectMapper defaultObjectMapper;

    @Autowired
    private CacheManager cacheManager;

    @Bean(name="defaultCache")
    public Cache getCache() throws IOException {
        CacheType cType = CacheType.getByType(cacheType);
        switch (cType) {
            case MEMCACHED:
                return getMemcache();

            case COUCHBASE:
                return getCouchbaseCache();

            case EHCACHE:
                return getEhCache();

            default:
                throw new IllegalStateException("something is terribly wrong.. should never reach here");
        }
    }

    private Cache getCouchbaseCache() throws IOException {
        CouchbaseFactory factory = new CouchbaseFactory();
        factory.setReadTimeout(cacheReadTimeout);
        factory.setWriteTimeout(cacheWriteTimeout);
        factory.setEnqueueTimeout(cacheEnqueueTimeout);
        factory.setConnectionURI(couchbaseCacheConnectionURI);
        factory.setTranscoder(new JsonTranscoder(Object.class, getObjectMapper()));
        CouchbaseImpl cache = new CouchbaseImpl();
        cache.setObjectMapper(getObjectMapper());
        cache.setCache((CouchbaseClientIF) factory.getObject());

        return cache;
    }

    private Cache getMemcache() throws IOException {
        MemcachedFactory factory = new MemcachedFactory();
           factory.setReadTimeout(cacheReadTimeout);
           factory.setWriteTimeout(cacheWriteTimeout);
           factory.setCacheLocations(memcachedCacheConnectionURI);
           factory.setTranscoder(new JsonTranscoder(Object.class, getObjectMapper()));
           MemcachedImpl cache = new MemcachedImpl();
           cache.setObjectMapper(getObjectMapper());
           cache.setCache((MemcachedClientIF) factory.getObject());

           return cache;
    }

    protected Cache getEhCache() throws IOException {
        EhCacheImpl cache = new EhCacheImpl();
        cache.setCache(cacheManager.getCache("product"));

           return cache;
    }

    @Bean(name="defaultObjectMapper")
    public ObjectMapper getObjectMapper() {
        if(defaultObjectMapper==null) {
            ObjectMapper mapper = new ObjectMapper();
            AnnotationIntrospector primary = new JaxbAnnotationIntrospector();
            AnnotationIntrospector secondary = new JacksonAnnotationIntrospector();
            AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);
            mapper.getDeserializationConfig().setAnnotationIntrospector(pair);
            mapper.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.getSerializationConfig().setAnnotationIntrospector(pair);
            mapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
            mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
            this.defaultObjectMapper = mapper;
        }
        return defaultObjectMapper;
    }
}