package com.transfer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BlockerApiService {

    private final RestClient blockerServiceClient;

    public BlockerApiService(RestClient.Builder builder) {
        this.blockerServiceClient = builder.baseUrl("http://blocker-api/api").build();
    }

    @CircuitBreaker(name = "cbservice")
    public Boolean validate(){
        return blockerServiceClient.get()
                .uri("/validate")
                .retrieve()
                .body(Boolean.class);
    }
}
