package com.technolearn.ms.product.dto;

import java.math.BigDecimal;

import lombok.Builder;

@Builder
public record ProductResponse(String id,String name, String description, BigDecimal price, Integer inventoryQty) {}
