package com.generator.service;

import com.generator.dto.ExchangeDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExchangeApiService {

    private final RestClient exchangeServiceClient;

    public ExchangeApiService(RestClient.Builder builder) {
        this.exchangeServiceClient = builder.baseUrl("http://exchange-api/api").build();
    }

    @CircuitBreaker(name = "cbservice")
    public void setExchange(ExchangeDto exchangeDto) {
        exchangeServiceClient.post()
                .uri("/exchange")
                .body(exchangeDto)
                .retrieve()
                .toBodilessEntity();
    }
}
