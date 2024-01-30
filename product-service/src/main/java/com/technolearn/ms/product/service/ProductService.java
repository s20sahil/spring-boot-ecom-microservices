package com.technolearn.ms.product.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.technolearn.ms.product.dto.ProductRequest;
import com.technolearn.ms.product.model.Product;
import com.technolearn.ms.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void saveProduct(ProductRequest productReq) {
        Product product = Product.builder()
                .name(productReq.name())
                .description(productReq.description())
                .price(productReq.price())
                .build();
        productRepository.save(product);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String queryString) {
        return productRepository.findByNameContainingIgnoreCase(queryString);
    }

}
