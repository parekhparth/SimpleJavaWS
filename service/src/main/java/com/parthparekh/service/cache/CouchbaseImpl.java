package com.parthparekh.service.cache;

import com.couchbase.client.CouchbaseClientIF;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.Future;

/**
 * Couchbase cache implementation
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
public class CouchbaseImpl implements Cache {
    private static Logger logger = LoggerFactory.getLogger(CouchbaseImpl.class);
    private CouchbaseClientIF cache;

    @Autowired
    @Qualifier(value = "defaultObjectMapper")
    private ObjectMapper objectMapper;
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setCache(CouchbaseClientIF cache) {
        this.cache = cache;
    }
    
    public <T> boolean put(String key, T value) {
        return put(key, value, 0);
    }

    @Override
    public <T> boolean put(String key, T value, int ttl) {
        Future<Boolean> success = cache.set(key, ttl, value);
        try {
            return success.get().booleanValue();
        }
        catch (Exception e) {
            logger.warn("unable to update cache: key=" + key + "; value=" + value + "; e=" + e);
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
            logger.warn("unable to evict cache: key=" + key + "; e=" + e);
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
   		return CacheType.COUCHBASE;
   	}
}