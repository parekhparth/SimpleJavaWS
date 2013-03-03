package com.parthparekh.service.cache;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseClientIF;
import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.ConnectionObserver;
import net.spy.memcached.DefaultHashAlgorithm;
import net.spy.memcached.FailureMode;
import net.spy.memcached.transcoders.Transcoder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Couchbase client factory
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
public class CouchbaseFactory implements FactoryBean
{
    private static Logger logger = LoggerFactory.getLogger(CouchbaseFactory.class);

	private long readTimeout;
	private long writeTimeout;
    private long enqueueTimeout;
	private String connectionURI;
    private String bucketName;
    private String userName;
    private String password;
	
	private Transcoder<Object> transcoder;
	
	public void setReadTimeout(long readTimeout) {
    	this.readTimeout = readTimeout;
    }

	public void setWriteTimeout(long writeTimeout) {
    	this.writeTimeout = writeTimeout;
    }

    public void setConnectionURI(String connectionURI) {
    	this.connectionURI = connectionURI;
    }

    public void setTranscoder(Transcoder<Object> transcoder) {
        this.transcoder = transcoder;
    }
	
	
	@Override
    public Object getObject() throws IOException {
		List<URI> addresses = getAddresses(connectionURI);
		long timeout = Math.max(readTimeout, writeTimeout);
        //TODO make all the below properties configurable via properties file
        ConnectionFactoryBuilder builder = new CouchbaseConnectionFactoryBuilder()
                .setOpTimeout(timeout)                      // wait up to timeout seconds for an operation to succeed
                .setOpQueueMaxBlockTime(enqueueTimeout)     // wait up to 'enqueueTimeout' seconds when trying to enqueue an operation
                .setDaemon(true)
                .setProtocol(Protocol.BINARY)
                .setHashAlg(DefaultHashAlgorithm.KETAMA_HASH)
                .setFailureMode(FailureMode.Redistribute)
                .setInitialObservers(Collections.singleton((ConnectionObserver) new CouchbaseAlerter()));
        if(transcoder!=null) {
            builder.setTranscoder(transcoder);
        }

        //assuming there isn't any password set for Couchbase
        CouchbaseConnectionFactory connectionFactory
			= ((CouchbaseConnectionFactoryBuilder)builder).buildCouchbaseConnection(addresses, "default", "", "");
		
		return new CouchbaseClient(connectionFactory);
	}

    protected List<URI> getAddresses(String connectionURI) {
        Assert.notNull(connectionURI, "couchbase connection URI is null");
        String[] urls = StringUtils.split(connectionURI, ", ");
        Assert.isTrue(urls.length>0, "couchbase connection URI is invalid");
        List<URI> uriList = new ArrayList<URI>();

        for(int i=0; i<urls.length; i++) {
            URI uri = URI.create(urls[i]);
            if(uri==null) {
                logger.error("invalid connection URI: " + uri);
                continue;
            }
            uriList.add(uri);
        }
        Assert.notEmpty(uriList, "couchbase connection uri list is empty (connection string is invalid)");
        return uriList;
    }


	@Override
    public Class<CouchbaseClientIF> getObjectType() {
	    return CouchbaseClientIF.class;
    }
	
	@Override
    public boolean isSingleton() {
	    return false;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnqueueTimeout(long enqueueTimeout) {
        this.enqueueTimeout = enqueueTimeout;
    }

    static class CouchbaseAlerter implements ConnectionObserver {
		private static final Logger logger = LoggerFactory.getLogger(CouchbaseAlerter.class);

		@Override
        public void connectionEstablished(SocketAddress sa, int reconnectCount) {
			logger.info("couchbase connection: addr=" + sa);
        }

		@Override
        public void connectionLost(SocketAddress sa) {
			logger.warn("couchbase connection: addr=" + sa);
        }
	}
}