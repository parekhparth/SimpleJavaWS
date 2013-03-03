package com.parthparekh.service.utils;

import com.parthparekh.api.Product;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Utility class for Product Service
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@Component
public class ProductServiceUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceUtils.class);
    private static final String PRODUCT_CACHE_PREFIX = "productCachePrefix";

    public void validateProductForUpdate(Product oldProduct, Product newProduct) {
        Assert.notNull(oldProduct, "product object stored in db is null");
        Assert.notNull(newProduct, "product object to update cannot be null");
        if(StringUtils.isBlank(newProduct.getName())) {
            logger.debug("name cannot be blank during update");
            newProduct.setName(oldProduct.getName());
        }
        if(newProduct.getPrice()==null) {
            logger.debug("price cannot be null during update");
            newProduct.setPrice(oldProduct.getPrice());
        }
    }

    public String generateProductCacheKey(String id) {
        return PRODUCT_CACHE_PREFIX + "." + id;
    }
}
