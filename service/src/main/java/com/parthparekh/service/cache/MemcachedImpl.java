package com.parthparekh.service.cache;

import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.Future;

/**
 * Memcache cache implementation
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
public class MemcachedImpl implements Cache {
    private static Logger logger = LoggerFactory.getLogger(MemcachedImpl.class);
    private MemcachedClientIF cache;

    @Autowired
    @Qualifier(value = "defaultObjectMapper")
    private ObjectMapper objectMapper;
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setCache(MemcachedClientIF cache) {
        this.cache = cache;
    }


    @Override
    public <T> boolean put(String key, T value) {
        return put(key, value, 0);
    }

    @Override
    public <T> boolean put(String key, T value, int ttl) {
        Future<Boolean> success = cache.set(key, ttl, value);
        try {
            logger.info("data added to memcached: " + ((MemcachedClient)cache).getNodeLocator().getPrimary(key).getSocketAddress());
            return success.get().booleanValue();
        }
        catch (Exception e) {
            logger.warn("unable to update cache: key=" + key + "; value=" + value + "; " + e);
        }

        return false;
    }

    @Override
    public boolean evict(String key) {
        Future<Boolean> success = cache.delete(key);
        try {
            return success.get().booleanValue();
        }
        catch (Exception e) {
            logger.warn("unable to evict cache: key=" + key + "; " + e);
        }
        return false;
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object object = cache.get(key, new JsonTranscoder(clazz, objectMapper));
        return (T) object;
    }

    @Override
   	public CacheType getType() {
   		return CacheType.MEMCACHED;
   	}
}
