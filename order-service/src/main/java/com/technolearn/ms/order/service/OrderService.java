package com.technolearn.ms.order.service;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.technolearn.ms.order.dto.InventoryRequest;
import com.technolearn.ms.order.dto.InventoryResponse;
import com.technolearn.ms.order.dto.OrderLineItemDto;
import com.technolearn.ms.order.dto.OrderRequest;
import com.technolearn.ms.order.model.Order;
import com.technolearn.ms.order.model.OrderLineItem;
import com.technolearn.ms.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final WebClient.Builder webClientBuilder;

    @Value(value = "${inventory.service.endpoint}")
    private String inventoryServiceEndpoint;

    public String placeOrder(OrderRequest orderRequest) {

        Order order = new Order();
        //Set UUID as a new order number
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItem> orderLineItems = orderRequest.orderLineItems()
                .parallelStream()
                .map(oli -> mapDtoToOrderLineItemModel(oli,order.getOrderNumber()))
                .toList();

        //Set mapped orderLineItems in the new order object
        order.setOrderLineItems(orderLineItems);

        //Prepare payload for making inventory service call for checking if inventory is avaialable
        List<String> productSkuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        InventoryResponse[] inventoryResponseArray = getInventoryIsInStockResponse(productSkuCodes);
        
        //Check if the quanity listed in request payload is less than inventory quantity for the sku
        Predicate<InventoryResponse> predicateQtyCheck = inventoryRes -> 
            orderRequest.orderLineItems().stream() //Stream order line items in the request payload which would have ordered qty for the sku
            .filter(orderli -> orderli.productSku().equals(inventoryRes.productSkuCode()))
            .findFirst()
            .get()
            .quantity() <= inventoryRes.quantity();
            

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(predicateQtyCheck);


                
        //Save the order in the DB
        if (allProductsInStock) {
            orderRepository.save(order);
            log.info("Order {} saved successfully", order.getOrderNumber());
            
            //TODO: replace this one by a bulk update call and make it blocking
            //Reduce the inventory quantity
            order.getOrderLineItems()
            .stream()
            .forEach(oli->{
                        InventoryResponse matchingObj = Arrays.stream(inventoryResponseArray)
                                .filter(invRes -> invRes.productSkuCode().equals(oli.getSkuCode())).findFirst().get();
                        InventoryRequest ir = new InventoryRequest(matchingObj.productSkuCode(),
                                matchingObj.quantity() - oli.getQuantity());
                        updateInventoryForProductSku(ir).subscribe(
                            result -> {
                                // Handle successful result
                                System.out.println("Inventory updated successfully");
                            },
                            error -> {
                                // Handle error
                                System.err.println("Error updating inventory: " + error.getMessage());
                            }
                        );;
                    });
            
            return order.getOrderNumber();
        } else {
            String outOfStockSkusMsg = """
                SKU %s are not in stock, please try again later
                """.formatted(Arrays.stream(inventoryResponseArray).filter(predicateQtyCheck.negate()).map(inv->inv.productSkuCode()).toList().toString());
            log.error(outOfStockSkusMsg);  
            throw new IllegalArgumentException(outOfStockSkusMsg);
        }

    }

    /**
     * To make call to the get Inventory API from inventory service
     * @param productSkuCodes
     * @return
     */
    private InventoryResponse[] getInventoryIsInStockResponse(List<String> productSkuCodes) {
        return webClientBuilder.build().get()
                .uri(inventoryServiceEndpoint,
                        uriBuilder -> uriBuilder.queryParam("skuCode", productSkuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
    }
    
    private Mono<Void> updateInventoryForProductSku(InventoryRequest updateInventoryRequest) {

        return webClientBuilder.build().patch()
                .uri(inventoryServiceEndpoint,uriBuilder -> uriBuilder.path("/skus/{skuCode}").build(updateInventoryRequest.productSkuCode()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateInventoryRequest)
                .exchangeToMono(response -> {
                    if (!response.statusCode().is2xxSuccessful()) {
                        return Mono.error(new Exception("Inventory update failed for "+updateInventoryRequest.productSkuCode()));
                    }
                    return Mono.empty(); // Return empty Mono if the update is successful
                });
    }
    
    /**
     * Extracting method for better readability
     * @param oli
     * @return
     */
    private OrderLineItem mapDtoToOrderLineItemModel(OrderLineItemDto oli, String orderNumber) {
        return OrderLineItem.builder()
        .skuCode(oli.productSku())
        .price(oli.price())
                .quantity(oli.quantity())
        .orderNumber(orderNumber)
        .build();
    }

    
}
