package com.technolearn.ms.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technolearn.ms.product.dto.ProductRequest;
import com.technolearn.ms.product.repository.ProductRepository;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductServiceApplicationTests {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    //TestContainer package will dynamically update the mongoDB URI upon container start
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll(); // Clear MongoDB data before each test method
    }

    @Test
    void shouldCreateProducts() throws Exception {
        Arrays.stream(getProductRequests()).forEach(
                productRequest -> {
                    try {
                        String productRequestString = objectMapper.writeValueAsString(productRequest);
                        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productRequestString))
                                .andExpect(status().isCreated());
                    } catch (Exception e) {
                        Assertions.fail();
                    }
                });
        Assertions.assertEquals(getProductRequests().length, productRepository.findAll().size());
    }
    
    @Test
    void shouldFindOnlyGivenProducts() throws Exception {
        //Data seeding
        Arrays.stream(getProductRequests()).forEach(
                productRequest -> {
                    try {
                        String productRequestString = objectMapper.writeValueAsString(productRequest);
                        mockMvc.perform(MockMvcRequestBuilders.post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productRequestString))
                                .andExpect(status().isCreated());
                    } catch (Exception e) {
                        Assertions.fail();
                    }
                });
        Assertions.assertEquals(getProductRequests().length, productRepository.findAll().size());

        //Search method tests
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                .param("q", "Product A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Product A"));//Since response is an array, getting the name attribute of the first element
        
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                .param("q", "product b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Product B"));//Since response is an array, getting the name attribute of the first element
        
        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                .param("q", "product"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(3)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/products/search")
                .param("q", "noResultExpected!"))
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    private ProductRequest[] getProductRequests() {
        ProductRequest[] productRequest = {
            new ProductRequest("Product A","Product A dscription",BigDecimal.valueOf(10.99)),
            new ProductRequest("Product B","Product B dscription",BigDecimal.valueOf(200.99)),
            new ProductRequest("Product C","Product C dscription",BigDecimal.valueOf(49.99))
        };
        return productRequest;
    }

}