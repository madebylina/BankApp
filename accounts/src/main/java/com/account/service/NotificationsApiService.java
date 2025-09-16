package com.account.service;

import com.account.model.dto.NotificationDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationsApiService {

    private final RestClient notificationsServiceClient;

    public NotificationsApiService(RestClient.Builder builder) {
        this.notificationsServiceClient = builder.baseUrl("http://notifications-api/api").build();
    }

    /**
     * Отправляет уведомление во внешний сервис уведомлений через HTTP POST.
     * При этом вызов защищён Circuit Breaker с именем "cbservice",
     * чтобы предотвратить перегрузку при сбоях внешнего сервиса.
     */
    @CircuitBreaker(name = "cbservice")
    public void notificate(NotificationDto notificationDto) {
        notificationsServiceClient.post()
                .uri("/notifications")
                .body(notificationDto)
                .retrieve()
                .toBodilessEntity();
    }
}
