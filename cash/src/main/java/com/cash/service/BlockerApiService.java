package com.cash.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class BlockerApiService {

    private final RestClient blockerServiceClient;

    public BlockerApiService(@Qualifier("blockerApiClient") RestClient blockerServiceClient) {
        this.blockerServiceClient = blockerServiceClient;
    }

    @CircuitBreaker(name = "cbservice")
    public Boolean validate() {
        return blockerServiceClient.get()
                .uri("/validate")
                .retrieve()
                .body(Boolean.class);
    }
}
