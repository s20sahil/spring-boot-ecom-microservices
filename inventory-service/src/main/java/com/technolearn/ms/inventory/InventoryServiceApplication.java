package com.technolearn.ms.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.technolearn.ms.inventory.model.Inventory;
import com.technolearn.ms.inventory.repository.InventoryRepository;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	/*
	 * This method emulates the running java code from the command line
	 */
	@Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            Inventory inventory1 = new Inventory();
        inventory1.setProductSkuCode("SKU001");
        inventory1.setProductName("Product 1");
        inventory1.setQuantity(10);
        inventoryRepository.save(inventory1);

        Inventory inventory2 = new Inventory();
        inventory2.setProductSkuCode("SKU002");
        inventory2.setProductName("Product 2");
        inventory2.setQuantity(20);
        inventoryRepository.save(inventory2);

        Inventory inventory3 = new Inventory();
        inventory3.setProductSkuCode("SKU003");
        inventory3.setProductName("Product 3");
        inventory3.setQuantity(30);
        inventoryRepository.save(inventory3);

        Inventory inventory4 = new Inventory();
        inventory4.setProductSkuCode("SKU004");
        inventory4.setProductName("Product 4");
        inventory4.setQuantity(40);
        inventoryRepository.save(inventory4);
		};
    }

}
