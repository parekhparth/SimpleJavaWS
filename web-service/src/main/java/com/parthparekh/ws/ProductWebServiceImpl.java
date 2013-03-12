package com.parthparekh.ws;

import com.parthparekh.ProductService;
import com.parthparekh.ProductWebService;
import com.parthparekh.api.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Product web service implementation
 *
 * @author: Parth Parekh
 **/
@Path("products/v1")
@Service("productWebService")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductWebServiceImpl implements ProductWebService {
    private static final Logger logger = LoggerFactory.getLogger(ProductWebServiceImpl.class);

    @Autowired
    @Qualifier("cache")
    private ProductService productService;


    @Override
    @POST
    public Product createProduct(Product product) {
        return productService.createProduct(product);
    }

    @Override
    @GET
    @Path("{id}")
    public Product getProduct(@PathParam("id") String id) {
        Product product = productService.getProduct(id);
        return product;
    }

    @Override
    @GET
    public List<Product> getAll() {
        return productService.getAll();
    }

    @Override
    @PUT
    @Path("{id}")
    public Product updateProduct(@PathParam("id") String id, Product product) {
        return productService.updateProduct(id, product);
    }
}

