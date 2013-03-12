package com.parthparekh.service;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.parthparekh.api.Product;
import com.parthparekh.api.ProductStatus;
import com.parthparekh.service.cache.Cache;
import com.parthparekh.service.cache.EhCacheImpl;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.UnknownHostException;

/**
 * Utility functions for test
 *
 * @author: Parth Parekh
 **/
@Component
public class TestUtils {
    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

    @Autowired
    @Qualifier("defaultCache")
    private Cache cache;

    public Product createProduct() {
        Product product = new Product().setName("test product 1")
                .setDescription("test description 1")
                .setPrice(BigDecimal.ONE);
        return product;
    }

    public void clearDb() throws UnknownHostException {
        Mongo mongo = new Mongo("localhost", 27017);
        DB db = mongo.getDB("test");
        db.getCollection("product").drop();
        logger.info("mongoDb cleared");
    }

    public void clearCache() {
        // assuming it's ehcache
        ((EhCacheImpl) cache).removeAll();
        logger.info("cache cleared");
    }

    public void validateProduct(Product product) {
        Assert.assertNotNull(product);
        Assert.assertNotNull(product.getId());
        Assert.assertEquals("test product 1", product.getName());
        Assert.assertEquals("test description 1", product.getDescription());
        Assert.assertEquals(ProductStatus.ACTIVE, product.getStatus());
        Assert.assertEquals(BigDecimal.ONE, product.getPrice());
    }

    public void validateUpdatedProduct(Product expected, Product actual) {
        Assert.assertNotNull(expected);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.getDescription(), actual.getDescription());
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
        Assert.assertEquals(expected.getPrice(), actual.getPrice());
    }
}