package com.technolearn.ms.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technolearn.ms.inventory.repository.InventoryRepository;
import com.technolearn.ms.inventory.dto.InventoryRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class InventoryServiceApplicationTests {

    @Container
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:latest");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private InventoryRepository inventoryRepository;

    //TestContainer package will dynamically update the mongoDB URI upon container start
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        dymDynamicPropertyRegistry.add("spring.datasource.username", postgresContainer::getUsername);
        dymDynamicPropertyRegistry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll(); // Clear Postgres data before each test method
    }

    @Test
    void shouldCreateProductSkuInventory() throws Exception {
        Arrays.stream(getInventoryRequest()).forEach(
                inventoryRequest -> {
                    try {
                        String invenotryRequestString = objectMapper.writeValueAsString(inventoryRequest);
                        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invenotryRequestString))
                                .andExpect(status().isCreated());
                    } catch (Exception e) {
                        Assertions.fail();
                    }
                });
        Assertions.assertEquals(getInventoryRequest().length, inventoryRepository.findAll().size());
    }

    @Test
    void shouldReturnIsInStockTrue() throws Exception {
        Arrays.stream(getInventoryRequest()).forEach(
                inventoryRequest -> {
                    try {
                        String invenotryRequestString = objectMapper.writeValueAsString(inventoryRequest);
                        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invenotryRequestString))
                                .andExpect(status().isCreated());
                    } catch (Exception e) {
                        Assertions.fail();
                    }
                });
        Assertions.assertEquals(getInventoryRequest().length, inventoryRepository.findAll().size());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
        .param("skuCode","SKU00A","SKU00B","SKU00C","NOVALIDSKU"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].productSkuCode").value("SKU00A"))
                .andExpect(jsonPath("$[0].isInStock").value("true"))
                .andExpect(jsonPath("$[1].productSkuCode").value("SKU00B"))
                .andExpect(jsonPath("$[1].isInStock").value("true"))
                .andExpect(jsonPath("$[2].productSkuCode").value("SKU00C"))
                .andExpect(jsonPath("$[2].isInStock").value("false"));

    }


    @Test
    void shouldUpdateProductSkuInventory() throws Exception {
        Arrays.stream(getInventoryRequest()).forEach(
                inventoryRequest -> {
                    try {
                        String invenotryRequestString = objectMapper.writeValueAsString(inventoryRequest);
                        mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invenotryRequestString))
                                .andExpect(status().isCreated());

                        //Form update request
                        Integer newQty = new Random().nextInt(100);
                        InventoryRequest updatePayload = new InventoryRequest(null, null, newQty);

                        mockMvc.perform(MockMvcRequestBuilders
                                .patch("/api/inventory/skus/{id}",
                                        URLEncoder.encode(inventoryRequest.productSkuCode(),
                                                StandardCharsets.UTF_8.toString()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatePayload)))
                                .andExpect(status().isOk());
                        Assertions.assertEquals(inventoryRepository.findByProductSkuCodeIn(Arrays.asList(inventoryRequest.productSkuCode())).stream()
                                .findFirst().get().getQuantity(), newQty);                        
                        
                    } catch (Exception e) {
                        Assertions.fail();
                    }
                });
    }

    @Test
    void shouldGiveNotFoundCodeWhileUpdatingAbsentSku() throws Exception {
        InventoryRequest updatePayload = new InventoryRequest(null,null,100);
        mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/inventory/skus/{id}", URLEncoder.encode("INVALIDSKU", StandardCharsets.UTF_8.toString()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatePayload)))
                                .andExpect(status().isNotFound());
    }


    private InventoryRequest[] getInventoryRequest() {
        InventoryRequest[] inventoryRequest = {
            new InventoryRequest("SKU00A","Product A",100),
            new InventoryRequest("SKU00B","Product B",200),
            new InventoryRequest("SKU00C","Product C",0)
        };
        return inventoryRequest;
    }

}