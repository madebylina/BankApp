package com.front.service;

import com.front.dto.CashDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CashApiService {

    private final RestClient cashServiceClient;

    public CashApiService(RestClient.Builder builder) {
        this.cashServiceClient = builder.baseUrl("http://cash-api/api").build();
    }

    @CircuitBreaker(name = "cbservice")
    public void cash(CashDto cashDto) {
        cashServiceClient.post()
                .uri("/cash")
                .body(cashDto)
                .retrieve()
                .toBodilessEntity();
    }
}
