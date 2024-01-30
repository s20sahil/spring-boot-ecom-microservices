package com.technolearn.ms.order.dto;

public record InventoryResponse(String productSkuCode,String productName,Integer quantity, Boolean isInStock) {}
