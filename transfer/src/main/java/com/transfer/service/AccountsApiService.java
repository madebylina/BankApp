package com.transfer.service;

import com.transfer.dto.TransferDto;
import com.transfer.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import javax.management.OperationsException;

@Service
public class AccountsApiService {

    private final RestClient accountsServiceClient;

    // public AccountsApiService(RestClient.Builder builder, @Value("${appservices.accounts-api:http://accounts-api/api}") String baseUrl) {
    public AccountsApiService(RestClient.Builder builder,
                              @Value("${appservices.accounts-api:http://localhost:8081/api}") String baseUrl) {
        this.accountsServiceClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "cbservice")
    public void transfer(TransferDto transferDto) throws OperationsException {
        try {
            accountsServiceClient.post()
                    .uri("/account/transfer")
                    .body(transferDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException restClientResponseException) {
            throw new OperationsException(restClientResponseException.getResponseBodyAsString());
        }
    }

    @CircuitBreaker(name = "cbservice")
    public UserDto getUserById(Long userId) {
        return accountsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/account/user/{userId}")
                        .build(userId))
                .retrieve()
                .body(UserDto.class);
    }

}