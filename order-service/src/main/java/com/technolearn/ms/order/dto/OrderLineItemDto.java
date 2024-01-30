package com.technolearn.ms.order.dto;

import java.math.BigDecimal;

public record OrderLineItemDto(Long id, String productSku, BigDecimal price, Integer quantity) {
}
