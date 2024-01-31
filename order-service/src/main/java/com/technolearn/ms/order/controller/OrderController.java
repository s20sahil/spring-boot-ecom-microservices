package com.technolearn.ms.order.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.technolearn.ms.order.dto.OrderRequest;
import com.technolearn.ms.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Order Placement flow starts!");
        try{
            String orderNumber = orderService.placeOrder(orderRequest);
            log.info("New order {} created succesfully!",orderNumber);
            return "Order "+orderNumber+"  Placed Successfully";
        }catch(IllegalArgumentException iae){
            return iae.getLocalizedMessage();
        }
        
    }
    
}
