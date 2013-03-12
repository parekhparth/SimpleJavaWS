package com.parthparekh.api;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * Test to de-serialize Product object
 *
 * @author: Parth Parekh
 **/
public class ProductDeSerializationTest {

    private ObjectMapper objectMapper;
    private String deSerializeProduct1;
    private String deSerializeProduct2;
    private String deSerializeProduct3;

    @Before
    public void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        InputStream product1 = getClass().getClassLoader().getResourceAsStream("META-INF/json/product1.json");
        deSerializeProduct1 = IOUtils.toString(product1);
        IOUtils.closeQuietly(product1);

        InputStream product2 = getClass().getClassLoader().getResourceAsStream("META-INF/json/product2.json");
        deSerializeProduct2 = IOUtils.toString(product2);
        IOUtils.closeQuietly(product2);

        InputStream product3 = getClass().getClassLoader().getResourceAsStream("META-INF/json/product3.json");
        deSerializeProduct3 = IOUtils.toString(product3);
        IOUtils.closeQuietly(product3);
    }

    @Test
    public void testProductDeserialize() throws Exception {
        Product product = objectMapper.readValue(deSerializeProduct1, Product.class);
        Assert.assertEquals("Product 1", product.getName());
        Assert.assertEquals("description of product 1", product.getDescription());
        Assert.assertEquals(ProductStatus.ACTIVE, product.getStatus());
        Assert.assertEquals(BigDecimal.valueOf(0.99), product.getPrice());

        product = objectMapper.readValue(deSerializeProduct2, Product.class);
        Assert.assertEquals("Product 2", product.getName());
        Assert.assertEquals("description of product 2", product.getDescription());
        Assert.assertEquals(ProductStatus.DISABLED, product.getStatus());
        Assert.assertEquals(BigDecimal.valueOf(1.99), product.getPrice());

        product = objectMapper.readValue(deSerializeProduct3, Product.class);
        Assert.assertEquals("Product 3", product.getName());
    }
}