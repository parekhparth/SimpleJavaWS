package com.parthparekh;

import com.parthparekh.api.Product;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Interface for ProductWebService - defines the basic operations that Product web service offers
 *
 * @author: Parth Parekh
 **/
@Path("products/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProductWebService {

    /**
     * creates a product with name, description and price
     *
     * @param product - Product JSON containing name, description, status and price
     * @return returns the product that is persisted
     */
    @POST
    public Product createProduct(Product product);

    /**
     * retrieve product information from id
     *
     * @param id - product identifier
     * @return returns the product from db
     */
    @GET
    @Path("{id}")
    public Product getProduct(@PathParam("id") String id);

    /**
     * retrieves all the available products
     *
     * @return returns all the products from db
     */
    @GET
    public List<Product> getAll();

    /**
     * update product information
     *
     * @param id - product identifier for the existing product to update
     * @param product - Product JSON containing updated name, description, status and/or price
     * @return returns the product that was updated
     */
    @PUT
    @Path("{id}")
    public Product updateProduct(@PathParam("id") String id, Product product);
}