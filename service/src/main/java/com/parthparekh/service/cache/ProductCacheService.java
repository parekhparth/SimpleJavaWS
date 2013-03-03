package com.parthparekh.service.cache;

import com.parthparekh.ProductService;
import com.parthparekh.api.Product;
import com.parthparekh.service.exception.NoDataFoundException;
import com.parthparekh.service.utils.ProductServiceUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Caching layer for Product web service
 * for create/update - this service will first change the product in mongoDb (by calling productServiceImpl)
 * and then update the cache
 * for get - this service will first check the cache; if the value is not found, it will query the mongoDb
 * (by calling productSericeImpl)
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@Service
@Transactional
@Qualifier("cache")
public class ProductCacheService implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductCacheService.class);
    private static final String ALL_PRODUCTS_CACHE_KEY = "allProductsCacheKey";

    @Autowired
    @Qualifier("defaultCache")
    private Cache cache;

    @Autowired
    @Qualifier("default")
    private ProductService productService;

    @Autowired
    private ProductServiceUtils productServiceUtils;


    @Override
    public Product createProduct(Product product) {
        // clear id
        product.setId(null);
        Product persistedProduct = productService.createProduct(product);
        // save it in cache
        saveProductInCache(persistedProduct);
        logger.info("product created: " + persistedProduct);
        return persistedProduct;
    }

    @Override
    public Product getProduct(String id) {
        Assert.notNull(id, "product identifier cannot be null");
        Assert.isTrue(StringUtils.isNotBlank(id), "product identifier cannot be blank");
        String cacheKey = productServiceUtils.generateProductCacheKey(id);
        Product product = cache.get(cacheKey, Product.class);
        if(product==null) {
            product = productService.getProduct(id);
            // save the value in cache for next time
            if(product!=null) {
                if(cache.put(cacheKey, product)) {
                    logger.debug("product value cached: " + product);
                }
            }
            else {
                throw new NoDataFoundException("no product found for id: " + id);
            }
        }
        else {
            logger.debug("product value returned from cache: " + product);
        }
        logger.info("product found: " + product);
        return product;
    }

    @Override
    public List<Product> getAll() {
        List<Product> productList = cache.get(ALL_PRODUCTS_CACHE_KEY, List.class);
        if(productList==null) {
            productList = productService.getAll();
            // save the value in cache
            if(productList==null || productList.size()==0) {
                throw new NoDataFoundException("no products not found");
            }
            else {
                if(cache.put(ALL_PRODUCTS_CACHE_KEY, productList)) {
                    logger.debug("all products list cached; size: " + productList.size());
                }
            }
        }
        else {
            logger.debug("products list returned from cache; size: " + productList.size());
        }
        logger.info("total products found: " + productList.size());
        return productList;
    }

    @Override
    public Product updateProduct(String id, Product product) {
        Assert.notNull(id, "product identifier cannot be null");
        Assert.isTrue(StringUtils.isNotBlank(id), "product identifier cannot be blank");
        Product oldProduct = getProduct(id);
        if(oldProduct==null) {
            throw new IllegalArgumentException("no product found with id: " + id);
        }
        // below call will validate the product object for update
        productServiceUtils.validateProductForUpdate(oldProduct, product);
        Product newProduct = productService.updateProduct(id, product);
        // save the updated product in cache (it should overwrite the old product)
        saveProductInCache(newProduct);
        logger.info("product updated: " + newProduct);
        return newProduct;
    }

    private void saveProductInCache(Product product) {
        // save product in cache if not null
        if(product!=null) {
            String cacheKey = productServiceUtils.generateProductCacheKey(product.getId());
            if (cache.put(cacheKey, product)) {
                logger.debug("product value cached: " + product);
            }
            // clear all products cache
            cache.evict(ALL_PRODUCTS_CACHE_KEY);
            logger.debug("all products cache cleared");
        }
    }
}