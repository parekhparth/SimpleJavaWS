package com.parthparekh.service.cache;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.util.ShardInfo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.net.InetSocketAddress;
import java.io.IOException;

/**
 * Memcache cache implementation
 *
 * @author: George Tsiros
 * @author: Artemis Tsikiridis
 **/
public class RedisImpl implements Cache {
    private static Logger logger = LoggerFactory.getLogger(RedisImpl.class);
    private JedisCommands cache;

    @Autowired
    @Qualifier(value = "defaultObjectMapper")
    private ObjectMapper objectMapper;
    
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public void setCache(JedisCommands cache) {
        this.cache = cache;
    }

    @Override
    public <T> boolean put(String key, T value) {
        return put(key, value, 0);
    }

    @Override
    public <T> boolean put(String key, T value, int ttl) {
        String result;
	Class<?> clazz = value.getClass();
	JsonTranscoder transc = new JsonTranscoder(clazz,objectMapper);
	byte[] rawData = transc.serialize(value);
	if (ttl == 0 )  {
	    result = cache.set(key, new String(rawData));
	}
	else  {
	    result = cache.setex(key,ttl,new String(rawData));
	}
	if( result.equals("OK") )  {
	    ShardInfo shardInfo = ((ShardedJedis)cache).getShardInfo(key);
	    String host = ((JedisShardInfo)shardInfo).getHost();
	    int port = ((JedisShardInfo)shardInfo).getPort();
	    InetSocketAddress shardSocketAddress = new InetSocketAddress(host,port);
	    logger.info("data added to redis: " + shardSocketAddress );
	    return true;
	}
	else  {
            logger.warn("unable to update cache: key=" + key + "; value=" + value + "; " + result);
	}
	return false;
    }

   @Override
    public boolean evict(String key) {
        ShardedJedis shardedCache = (ShardedJedis) cache;
        long result = shardedCache.del(key);
	if( result == 1 )  {
	    return true;	  
	}
	else  {
	    logger.warn("unable to evict cache: key=" + key + "; " + result);
	}
        return false;	
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String object = cache.get(key);
	Object value;
	if( object.equals("null") )  {
	    return null;
	}
	try  {
           value = objectMapper.readValue(object, clazz);	    
	}
	catch(IOException e)  {
	    throw new IllegalArgumentException("cannot deserialize object: " + e);	
	}

	return (T) value;    
    }    

    @Override
       public CacheType getType() {
           return CacheType.REDIS;
       }
}
