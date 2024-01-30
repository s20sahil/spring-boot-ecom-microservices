package com.technolearn.ms.inventory.dto;

public record InventoryResponse(String productSkuCode,String productName,Integer quantity, Boolean isInStock) {}
