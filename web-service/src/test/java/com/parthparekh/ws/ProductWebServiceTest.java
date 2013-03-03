package com.parthparekh.ws;

import com.parthparekh.ProductWebService;
import com.parthparekh.api.Product;
import com.parthparekh.api.ProductStatus;
import com.parthparekh.service.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Test ProductWebService functions
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/test-application-context.xml" })
public class ProductWebServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ProductWebServiceTest.class);

    @Autowired
       private ProductWebService productWebService;

    @Autowired
    private TestUtils testUtils;

    private Product product;


    @Before
    public void setUp() throws UnknownHostException {
        testUtils.clearCache();
        testUtils.clearDb();
        product = testUtils.createProduct();
    }

    @Test
    public void createTest() {
        Product created = productWebService.createProduct(product);
        testUtils.validateProduct(created);
    }

    @Test
    public void getProductTest() {
        Product created = productWebService.createProduct(product);
        Product productFound = productWebService.getProduct(created.getId());
        Assert.assertEquals(created.getId(), productFound.getId());
        testUtils.validateProduct(productFound);
    }

    @Test
    public void updateProductTest() {
        Product created = productWebService.createProduct(product);
        Product updated = created.setName("updated name 1")
                .setDescription("updated description 1")
                .setStatus(ProductStatus.DISABLED)
                .setPrice(BigDecimal.TEN);
        Product actual = productWebService.updateProduct(created.getId(), updated);
        testUtils.validateUpdatedProduct(updated, actual);
    }

    @Test
    public void getAllProductsTest() throws UnknownHostException {
        testUtils.clearCache();
        testUtils.clearDb();
        Product created1 = productWebService.createProduct(product);
        Product created2 = productWebService.createProduct(product.clone());
        List<Product> productList = productWebService.getAll();
        Assert.assertEquals(2, productList.size());
        Assert.assertEquals(created1.getId(), productList.get(0).getId());
        Assert.assertEquals(created2.getId(), productList.get(1).getId());
        for(Product pr : productList) {
            testUtils.validateProduct(pr);
        }
    }
}