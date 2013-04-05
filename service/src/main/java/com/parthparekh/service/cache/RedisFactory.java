package com.parthparekh.service.cache;

import redis.clients.jedis.Connection;
import redis.clients.jedis.Client;
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
 * Memcache client factory
 *
 * @author: Artemis Tsikiridis
 * @author: George Tsiros
 **/

public class RedisFactory implements FactoryBean {
    private long readTimeout;
    private long writeTimeout;
    private String cacheLocations;

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setWriteTimeout(long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public void setCacheLocations(String cacheLocations) {
        this.cacheLocations = cacheLocations;
    }    

    @Override
    public Object getObject() {
        String[] urls = StringUtils.split(cacheLocations, ", ");
        List<InetSocketAddress> addresses = new ArrayList<InetSocketAddress>();
        for (String url : urls) {
            int colonIndex = url.indexOf(":");
            String host = url.substring(0, colonIndex);
            int port = NumberUtils.toInt(url.substring(colonIndex + 1, url.length()));
            if(port==0) {
                throw new FactoryBeanNotInitializedException("invalid port: cacheLocations=" + cacheLocations);
            }
            InetSocketAddress address = new InetSocketAddress(host, port);
            addresses.add(address);
        }
        long timeout = Math.max(readTimeout, writeTimeout);

	//TODO use other parameters by Jedis
	Client jedisClient = new Client(host,port);
	
	if( timeout == -1 ) {
	    jedisClient.setTimeoutInfinite();
	}
	else {
	    jedisClient.setTimeout(timeout);
	}
        return jedisClient;
	
	
    @Override
    public Class<MemcachedClientIF> getObjectType() {
        return JedisClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
        
	

    }	


}
