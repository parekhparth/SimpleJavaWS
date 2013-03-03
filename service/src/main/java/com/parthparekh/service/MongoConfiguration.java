package com.parthparekh.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

@Configuration
public class MongoConfiguration extends AbstractMongoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MongoConfiguration.class);

    @Value("${mongodb.name:test}")
    private String dbName;

    @Value("${mongodb.url}")
    private String dbUrl;

    @Value("${mongodb.user}")
    private String dbUser;

    @Value("${mongodb.password}")
    private String dbPassword;

    @Value("${mongodb.connections:10}")
    private int connections;

    @Value("${mongodb.connection.timeout:1000}")
    private int connectionTimeout;

    @Value("${mongodb.socket.timeout:1000}")
    private int socketTimeout;

    @Bean
    @Override
    public Mongo mongo() throws Exception {
        Mongo mongo = mongoFactory().getObject();
        return mongo;
    }

    public UserCredentials getUserCredentials() {
        if (StringUtils.hasLength(dbUser)) {
            return new UserCredentials(dbUser, dbPassword);
        } else {
            return null;
        }
    }

    @Bean
    public MongoFactoryBean mongoFactory() throws Exception {
        MongoOptions mongoOptions = new MongoOptions();
        mongoOptions.connectionsPerHost = connections;
        MongoFactoryBean mongoFactoryBean = new MongoFactoryBean();
        //check multiple urls for replica sets
        if(isReplicaSet()) {
            logger.debug("connecting to mongo replica sets");
            mongoFactoryBean.setReplicaSetSeeds(getServerAddresses());
            mongoOptions.autoConnectRetry = true;
            mongoOptions.connectTimeout = connectionTimeout;
            mongoOptions.socketTimeout = socketTimeout;
        }
        else {
            logger.debug("connecting to mongo master-slave");
            mongoFactoryBean.setHost(dbUrl);
        }
        mongoFactoryBean.setMongoOptions(mongoOptions);

        return mongoFactoryBean;
    }

    protected ServerAddress[] getServerAddresses() {
        Iterable<String> urlSplit = getReplicaSetsIterable();
        logger.debug("connecting to mongo url: " + urlSplit);
        List<ServerAddress> replicaSetList = new ArrayList<ServerAddress>();
        try {
            Iterator<String> urlIter = urlSplit.iterator();
            while(urlIter.hasNext()) {
                String serverAddress = urlIter.next();
                logger.debug("replica set address: " + serverAddress);
                StringTokenizer hostTokenizer = new StringTokenizer(serverAddress, ":");
                String host = hostTokenizer.nextToken();
                Integer port = Integer.valueOf(hostTokenizer.nextToken());
                ServerAddress address = new ServerAddress(host, port.intValue());
                replicaSetList.add(address);
            }

            ServerAddress[] serverAddresses = new ServerAddress[replicaSetList.size()];
            return replicaSetList.toArray(serverAddresses);
        }
        catch (UnknownHostException uhe) {
            logger.error("invalid host name for mongodb url: " + uhe.getMessage());
        }
        return null;
    }

    private Iterable<String> getReplicaSetsIterable() {
        return Splitter.on(",").trimResults().omitEmptyStrings().split(dbUrl);
    }

    protected boolean isReplicaSet() {
        Iterable<String> urlSplit = getReplicaSetsIterable();
        return Iterables.size(urlSplit) > 1;
    }

    @Bean(name={"productMongoTemplate"})
    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = super.mongoTemplate();
        if(isReplicaSet()) {
            // this will wait for at least 2 servers for the write operation before throwing exception
            mongoTemplate.setWriteConcern(WriteConcern.REPLICAS_SAFE);
        }
        else {
            // required by spring-data to re-throw server exception on the client end
            mongoTemplate.setWriteConcern(WriteConcern.SAFE);
        }
        return mongoTemplate;
    }

    @Override
    public String getDatabaseName() {
        return dbName;
    }
}