package com.front.service;

import com.front.dto.AccountDto;
import com.front.dto.CurrencyEnum;
import com.front.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class AccountsApiService {
    private final RestClient accountsServiceClient;

   // public AccountsApiService(RestClient.Builder builder, @Value("${appservices.accounts-api:http://accounts-api/api}") String baseUrl) {
   public AccountsApiService(RestClient.Builder builder,
                             @Value("${appservices.accounts-api:http://localhost:8081/api}") String baseUrl) {
        this.accountsServiceClient = builder.baseUrl(baseUrl).build();
    }

    @CircuitBreaker(name = "cbservice")
    public List<UserDto> getUsers() {
        return accountsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/account/users")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<UserDto>>() {
                });
    }

    @CircuitBreaker(name = "cbservice")
    public UserDto getUserByName(String username) {
        return accountsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/account/user")
                        .queryParam("username", username)
                        .build())
                .retrieve()
                .body(UserDto.class);
    }

    @CircuitBreaker(name = "cbservice")
    public UserDto saveUser(UserDto userDto) {
        return accountsServiceClient.post()
                .uri("/account/user")
                .body(userDto)
                .retrieve()
                .body(UserDto.class);
    }

    @CircuitBreaker(name = "cbservice")
    public AccountDto getAccountByUserAndCurrency(Long userId, CurrencyEnum currency) {
        return accountsServiceClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/account/value/{userId}/{currency}")
                        .build(userId, currency))
                .retrieve()
                .body(AccountDto.class);
    }

    @CircuitBreaker(name = "cbservice")
    public AccountDto saveAccount(AccountDto accountDto) {
        return accountsServiceClient.post()
                .uri("/account")
                .body(accountDto)
                .retrieve()
                .body(AccountDto.class);
    }

    @CircuitBreaker(name = "cbservice")
    public void deleteAccount(AccountDto accountDto) {
        accountsServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/account/{id}")
                        .build(accountDto.getId()))
                .retrieve()
                .toBodilessEntity();
    }

}

