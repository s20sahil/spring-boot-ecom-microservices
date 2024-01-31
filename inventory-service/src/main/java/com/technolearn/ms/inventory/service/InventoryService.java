package com.technolearn.ms.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.technolearn.ms.inventory.dto.InventoryRequest;
import com.technolearn.ms.inventory.dto.InventoryResponse;
import com.technolearn.ms.inventory.model.Inventory;
import com.technolearn.ms.inventory.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public void createSkuInventory(InventoryRequest inventoryRequest) {
        inventoryRepository.save(Inventory.builder()
        .productSkuCode(inventoryRequest.productSkuCode())
        .quantity(inventoryRequest.quantity())
        .productName(inventoryRequest.productName())
        .build());
    }

    public boolean updateSkuInventory(String id, Integer quantity) {
        return inventoryRepository.updateQuantityByProductSkuCode(id, quantity)==1; //Return true if only one row is updated
    }

    public List<InventoryResponse> isInStock(List<String> skuCode) {
        log.info("Checking for SKU inventory for {}",skuCode);
        return inventoryRepository.findByProductSkuCodeIn(skuCode).stream()
                .map(sku -> new InventoryResponse(sku.getProductSkuCode(), sku.getProductName(), sku.getQuantity(), sku.getQuantity()>0))
                .toList();
    }

}
