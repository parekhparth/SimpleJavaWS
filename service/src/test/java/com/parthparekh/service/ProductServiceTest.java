package com.parthparekh.service;

import com.parthparekh.ProductService;
import com.parthparekh.api.Product;
import com.parthparekh.api.ProductStatus;
import com.parthparekh.service.cache.Cache;
import com.parthparekh.service.exception.NoDataFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Test ProductService functions
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/test-context.xml"})
public class ProductServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Autowired
    @Qualifier("cache")
    private ProductService productService;

    @Autowired
    @Qualifier("defaultCache")
    private Cache cache;

    @Autowired
    private TestUtils testUtils;

    protected Product product;


    @Before
    public void setUp() throws UnknownHostException {
        testUtils.clearCache();
        testUtils.clearDb();
        product = testUtils.createProduct();
    }

    @Test
    public void createProductTest() {
        Product created = productService.createProduct(product);
        testUtils.validateProduct(created);
    }

    @Test
    public void updateProductTest() {
        Product created = productService.createProduct(product);
        Product actual = created.setDescription("updated description 1")
                .setName("updated name 1")
                .setStatus(ProductStatus.DISABLED)
                .setPrice(BigDecimal.TEN);
        Product updated = productService.updateProduct(created.getId(), actual);
        testUtils.validateUpdatedProduct(updated, actual);
    }

    @Test
    public void getProductTest() {
        Product created1 = productService.createProduct(product);
        Product productFound = productService.getProduct(created1.getId());
        Assert.assertEquals(created1.getId(), productFound.getId());
        testUtils.validateProduct(productFound);
    }

    @Test
    public void productNotFoundTest() {
        exception.expect(NoDataFoundException.class);
        productService.getProduct("product-id-does-not-exist");
    }

    @Test
    public void nullGetAllTest() throws UnknownHostException {
        testUtils.clearCache();
        testUtils.clearDb();
        exception.expect(NoDataFoundException.class);
        List<Product> productList = productService.getAll();
    }

    @Test
    public void getAllTest() throws UnknownHostException {
        testUtils.clearCache();
        testUtils.clearDb();
        Product created1 = productService.createProduct(product);
        Product created2 = productService.createProduct(product.clone());
        List<Product> productList = productService.getAll();
        Assert.assertEquals(2, productList.size());
        Assert.assertEquals(created1.getId(), productList.get(0).getId());
        Assert.assertEquals(created2.getId(), productList.get(1).getId());
        for(Product pr : productList) {
            testUtils.validateProduct(pr);
        }
    }

    @Test
    public void cacheInvalidateTest() throws UnknownHostException {
        Product created1 = productService.createProduct(product);
        testUtils.clearCache();
        Product productFound = productService.getProduct(created1.getId());
        Assert.assertEquals(created1.getId(), productFound.getId());
        testUtils.validateProduct(productFound);
    }
}