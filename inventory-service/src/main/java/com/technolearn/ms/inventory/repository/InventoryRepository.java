package com.technolearn.ms.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.technolearn.ms.inventory.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProductSkuCodeIn(List<String> skuCode);

    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = :quantity WHERE i.productSkuCode = :productSkuCode")
    int updateQuantityByProductSkuCode(String productSkuCode, Integer quantity);
    
} 