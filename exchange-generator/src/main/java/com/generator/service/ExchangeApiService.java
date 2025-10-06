package com.generator.service;

import com.generator.dto.ExchangeDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExchangeApiService {

    private final RestClient exchangeServiceClient;

    public ExchangeApiService(RestClient.Builder builder,
                              @Value("${appservices.exchange-api:http://exchange-api/api}") String baseUrl) {
        this.exchangeServiceClient = builder.baseUrl(baseUrl).build();
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
