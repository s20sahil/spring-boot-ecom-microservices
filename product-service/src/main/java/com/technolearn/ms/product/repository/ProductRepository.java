package com.technolearn.ms.product.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.technolearn.ms.product.model.Product;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    
    //Declare specific search method
    public List<Product> findByNameContainingIgnoreCase(String queryString);
}
