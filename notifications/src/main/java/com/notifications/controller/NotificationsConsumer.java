package com.notifications.controller;

import com.notifications.dto.NotificationDto;
import com.notifications.service.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.internals.Acknowledgements;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class NotificationsConsumer {

    private final JwtDecoder jwtDecoder;
    private final NotificationsService notificationsService;

    @KafkaListener(topics = "notification", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(NotificationDto notificationDto, @Header("Authorization") String authorizationHeader, Acknowledgment ack) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            System.err.println("JWT token is missing");
            return;
        }
        String token = authorizationHeader.substring(7);
        try {
            Jwt jwt = jwtDecoder.decode(token);

            List<String> roles = ((Map<String, List<String>>) jwt.getClaim("realm_access")).get("roles");
            if (roles == null || !roles.contains("ROLE_NORIFICATIONS")) {
                System.err.println("User does not have ROLE_EXCHANGE");
                return;
            }
            notificationsService.sendNotification(notificationDto);
            ack.acknowledge();
        } catch (JwtException e) {
            System.err.println("Invalid JWT: " + e.getMessage());
        }
    }
}
