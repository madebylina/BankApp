package com.transfer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BlockerApiService {

    private final RestClient blockerServiceClient;

    public BlockerApiService(RestClient.Builder builder,
                             @Value("${appservices.blocker-api:http://blocker-api/api}") String baseUrl) {
        this.blockerServiceClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "cbservice")
    public Boolean validate(){
        return blockerServiceClient.get()
                .uri("/validate")
                .retrieve()
                .body(Boolean.class);
    }
}
