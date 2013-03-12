package com.parthparekh;


import com.parthparekh.api.Product;

import java.util.List;

/**
 * Interface for ProductService - defines the basic operations that Product service offers
 *
 * @author: Parth Parekh
 **/
public interface ProductService {

    /**
     * creates a product with name, description and price
     *
     * @param product - product containing name, description, status and price
     * @return returns the product persisted in db
     */
    public Product createProduct(Product product);

    /**
     * retrieves product information from id
     *
     * @param id - product id
     * @return returns the product from db
     */
    public Product getProduct(String id);

    /**
     * retrieves all the available products
     *
     * @return returns all the products stored in db
     */
    public List<Product> getAll();

    /**
     * update product information
     *
     * @param id - product identifier to update
     * @param product - product object containing updated fields
     * @return returns the updated product
     */
    public Product updateProduct(String id, Product product);
}
