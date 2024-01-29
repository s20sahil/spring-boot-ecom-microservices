package com.technolearn.ms.product.controller;

import org.springframework.web.bind.annotation.RestController;

import com.technolearn.ms.product.dto.ProductRequest;
import com.technolearn.ms.product.dto.ProductResponse;
import com.technolearn.ms.product.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;



@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productRequest) {
        productService.saveProduct(productRequest);
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.findAllProducts()
        .stream()
        .map(product -> new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                null))
        .toList();
    }

    @GetMapping("/search")
    public List<ProductResponse> searchProducts(@RequestParam String q) {
        return productService.searchProducts(q).stream().map(product -> new ProductResponse(
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getPrice(),
                null)).toList();
    }
    
}
