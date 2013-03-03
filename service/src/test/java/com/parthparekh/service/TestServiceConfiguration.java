package com.parthparekh.service;

import com.parthparekh.service.cache.Cache;
import com.parthparekh.service.cache.EhCacheImpl;
import net.sf.ehcache.CacheManager;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Configuration class for Tests
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@Configuration
public class TestServiceConfiguration {
    @Value("${cache.type:ehcache}")
    private String cacheType;

    @Autowired
    private CacheManager cacheManager;


    @Bean(name="defaultCache")
    public Cache getCache() throws IOException {
        EhCacheImpl cache = new EhCacheImpl();
        cache.setCache(cacheManager.getCache("test-product"));
        return cache;
    }

    @Bean(name="defaultObjectMapper")
    public ObjectMapper getObjectMapper() {
        ServiceConfiguration sc = new ServiceConfiguration();
        return sc.getObjectMapper();
    }
}