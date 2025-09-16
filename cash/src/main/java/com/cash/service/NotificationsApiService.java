package com.cash.service;

import com.cash.dto.NotificationDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationsApiService {

    private final RestClient notificationsServiceClient;

    public NotificationsApiService(RestClient.Builder builder) {
        this.notificationsServiceClient = builder.baseUrl("http://notifications-api/api").build();
    }

    @CircuitBreaker(name = "cbservice")
    public void notificate(NotificationDto notificationDto) {
        notificationsServiceClient.post()
                .uri("/notifications")
                .body(notificationDto)
                .retrieve()
                .toBodilessEntity();
    }
}
