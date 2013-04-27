package com.parthparekh.service.cache;

import redis.clients.jedis.Connection;
import redis.clients.jedis.Client;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.util.MurmurHash;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Redis client factory
 *
 * @author: Artemis Tsikiridis
 * @author: George Tsiros
 **/

public class RedisFactory implements FactoryBean {
    private long readTimeout;
    private long writeTimeout;
    private String cacheLocations;
    private String weightOfHosts;

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public void setCacheLocations(String cacheLocations) {
        this.cacheLocations = cacheLocations;
    }

    public void setWeightOfHosts(String weightOfHosts) {
        this.weightOfHosts = weightOfHosts;
    }    

    @Override
    public Object getObject() {
        String[] urls = StringUtils.split(cacheLocations, ", ");
        String[] weights  = StringUtils.split(weightOfHosts, ", ");
	if (urls.length < weights.length) {
                throw new FactoryBeanNotInitializedException("invalid number of weights: weightOfHosts=  " + weightOfHosts + " (Number of weights > Number of urls)");
        }            
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        long timeout = Math.max(readTimeout, writeTimeout);
        for ( int i = 0; i < urls.length; i++) {
            int colonIndex = urls[i].indexOf(":");
            String host = urls[i].substring(0, colonIndex);
            int port = NumberUtils.toInt(urls[i].substring(colonIndex + 1, urls[i].length()));
            if(port==0) {
                throw new FactoryBeanNotInitializedException("invalid port: cacheLocations=" + cacheLocations);
            }
	    if( weights[i] == null) {
	        weights[i] = "1";
            }
	    int weight = Integer.parseInt(weights[i]);
            JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, (int) timeout,weight);
            shards.add(jedisShardInfo);
        }	
        MurmurHash murmurHash = new MurmurHash();
	ShardedJedis jedisClient = new ShardedJedis(shards,murmurHash);	
        return jedisClient;
    }	
	
    @Override
    public Class<ShardedJedis> getObjectType() {
        return ShardedJedis.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
        
}
