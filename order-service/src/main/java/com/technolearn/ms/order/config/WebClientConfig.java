package com.technolearn.ms.order.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().filter((request, next) -> {
            // Forward trace headers
            ClientRequest filteredRequest = ClientRequest.from(request)
                    .headers(headers -> headers.addAll(request.headers()))
                    .build();
            return next.exchange(filteredRequest);
        });
    }
}
