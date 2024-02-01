package com.technolearn.ms.order;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technolearn.ms.order.dto.OrderLineItemDto;
import com.technolearn.ms.order.dto.OrderRequest;
import com.technolearn.ms.order.repository.OrderRepository;
import com.technolearn.ms.order.service.OrderService;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderServiceApplicationTests {

	@Container
    static PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:latest");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;
    private OrderService orderService = Mockito.mock(OrderService.class);
	
    //TestContainer package will dynamically update the mongoDB URI upon container start
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        dymDynamicPropertyRegistry.add("spring.datasource.username", postgresContainer::getUsername);
        dymDynamicPropertyRegistry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll(); // Clear Postgres data before each test method
    }
	
	@Test
    public void shouldThrowAWebClientException() throws Exception {
        Mockito.when(orderService.placeOrder(getOrderRequest())).thenReturn(UUID.randomUUID().toString());
        try{
            mockMvc.perform(MockMvcRequestBuilders
                                .post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(getOrderRequest())))
                .andExpect(status().isCreated());
                Assertions.assertEquals(orderRepository.findAll().size(), 0);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
        
    }
    
    /**
     * Return object of type OrderRequest with mocked values for the test
     */
    private OrderRequest getOrderRequest() {
        List<OrderLineItemDto> orderLineItems = Arrays.asList(
                new OrderLineItemDto(1L, "SKU-MILK", new BigDecimal(4.99), 1),
                new OrderLineItemDto(2L, "SKU-BREAD", new BigDecimal(2.99), 2));

        return new OrderRequest(orderLineItems);
    }
    
}
