package com.technolearn.ms.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.technolearn.ms.inventory.dto.InventoryRequest;
import com.technolearn.ms.inventory.dto.InventoryResponse;
import com.technolearn.ms.inventory.service.InventoryService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createSkuInventory(@RequestBody InventoryRequest inventoryRequest) {
        inventoryService.createSkuInventory(inventoryRequest);
    }
    
    @PatchMapping("skus/{id}")
    public ResponseEntity<Void> updateSkuInventory(@PathVariable String id, @RequestBody InventoryRequest ir) {
        Boolean updateFlag = inventoryService.updateSkuInventory(id, ir.quantity());
        return updateFlag?ResponseEntity.ok().build():ResponseEntity.notFound().build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }
    
}
