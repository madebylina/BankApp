package com.account.service;

import com.account.configuration.OAuth2TokenProvider;
import com.account.model.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class NotificationsProducer {

    private final OAuth2TokenProvider tokenProvider;
    private final KafkaTemplate<String, NotificationDto> kafkaTemplate;

    public void notificate(NotificationDto notificationDto) {
        String token = tokenProvider.getAccessToken();

        ProducerRecord<String, NotificationDto> record =
                new ProducerRecord<>("notification", notificationDto.getLogin(), notificationDto);
        record.headers().add("Authorization", ("Bearer " + token).getBytes(StandardCharsets.UTF_8));

        kafkaTemplate.send(record);
    }
}
