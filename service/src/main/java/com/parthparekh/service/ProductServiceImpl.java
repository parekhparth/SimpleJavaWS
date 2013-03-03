package com.parthparekh.service;

import com.google.common.collect.Lists;
import com.parthparekh.ProductService;
import com.parthparekh.api.Product;
import com.parthparekh.service.db.ProductRepository;
import com.parthparekh.service.utils.ProductServiceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation for Product service
 *
 * @author: Parth Parekh (parthparekh [at] gatech [dot] edu)
 **/
@Service
@Transactional
@Qualifier("default")
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductServiceUtils productServiceUtils;


    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProduct(String id) {
        return productRepository.findOne(id);
    }

    @Override
    public List<Product> getAll() {
        Iterable<Product> productIterable = productRepository.findAll();
        return Lists.newArrayList(productIterable);
    }

    @Override
    public Product updateProduct(String id, Product product) {
        product.setId(id);
        return productRepository.save(product);
    }
}
