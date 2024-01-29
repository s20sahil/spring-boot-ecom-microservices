package com.technolearn.ms.product.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public final class Product {
    
    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;


}
