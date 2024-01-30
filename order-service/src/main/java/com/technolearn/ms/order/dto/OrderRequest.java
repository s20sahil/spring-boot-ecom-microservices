package com.technolearn.ms.order.dto;

import java.util.List;

public record OrderRequest(List<OrderLineItemDto> orderLineItems) {
    
}
