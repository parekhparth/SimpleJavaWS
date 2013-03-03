package com.parthparekh.service.db;

import com.parthparekh.api.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, String> {
}