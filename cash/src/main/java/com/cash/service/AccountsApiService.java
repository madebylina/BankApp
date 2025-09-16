package com.cash.service;

import com.cash.dto.CashDto;
import com.cash.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import javax.management.OperationsException;

@Service
public class AccountsApiService {

    private final RestClient accountServiceClient;

    public AccountsApiService(RestClient.Builder builder) {
        this.accountServiceClient = builder.baseUrl("http://accounts-api/api").build();
    }

    @CircuitBreaker(name = "cbservice")
    public void cash(CashDto cashDto) throws OperationsException {
        try {
            accountServiceClient.post()
                    .uri("/account/cash")
                    .body(cashDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException restClientResponseException) {
            throw new OperationsException(restClientResponseException.getResponseBodyAsString());
        }
    }

    @CircuitBreaker(name = "cbservice")
    public UserDto getUserById(Long userId) {
        return accountServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/account/user/{userId}")
                        .build(userId))
                .retrieve()
                .body(UserDto.class);
    }
}
