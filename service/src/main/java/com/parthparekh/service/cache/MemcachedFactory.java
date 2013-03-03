package com.parthparekh.service.cache;

import net.spy.memcached.*;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.transcoders.Transcoder;
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
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
public class MemcachedFactory implements FactoryBean {
	private long readTimeout;
	private long writeTimeout;
	private String cacheLocations;
	private Transcoder<Object> transcoder;

	
	public void setReadTimeout(long readTimeout) {
    	this.readTimeout = readTimeout;
    }

	public void setWriteTimeout(long writeTimeout) {
    	this.writeTimeout = writeTimeout;
    }

	public void setCacheLocations(String cacheLocations) {
    	this.cacheLocations = cacheLocations;
    }

    public void setTranscoder(Transcoder<Object> transcoder) {
        this.transcoder = transcoder;
    }
	
	@Override
    public Object getObject() throws IOException {
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

        //TODO make all the below properties configurable via properties file
        ConnectionFactoryBuilder builder = new ConnectionFactoryBuilder()
                .setOpTimeout(timeout)
                .setDaemon(true)
                .setProtocol(Protocol.BINARY)
                .setHashAlg(DefaultHashAlgorithm.KETAMA_HASH)
                .setFailureMode(FailureMode.Retry)
                .setInitialObservers(Collections.singleton((ConnectionObserver) new MemcachedAlerter()));
        
        if(transcoder!=null) {
            builder.setTranscoder(transcoder);
        }
        
        ConnectionFactory connectionFactory = builder.build();
		return new MemcachedClient(connectionFactory, addresses);
	}

	@Override
    public Class<MemcachedClientIF> getObjectType() {
	    return MemcachedClientIF.class;
    }
	
	@Override
    public boolean isSingleton() {
	    return false;
    }
	
	static class MemcachedAlerter implements ConnectionObserver {
		private static final Logger logger = LoggerFactory.getLogger(MemcachedAlerter.class);

		@Override
        public void connectionEstablished(SocketAddress sa, int reconnectCount) {
			logger.info("memcache connection: addr=" + sa);
        }

		@Override
        public void connectionLost(SocketAddress sa) {
			logger.warn("memcache connection: addr=" + sa);
        }
		
	}
}
