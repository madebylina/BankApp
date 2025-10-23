package com.generator.service;

import com.generator.configuration.OAuth2TokenProvider;
import com.generator.dto.ExchangeDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class ExchangeProducer {

    private final OAuth2TokenProvider tokenProvider;
    private final KafkaTemplate<String, ExchangeDto> kafkaTemplate;

    public void setExchange(ExchangeDto exchangeDto) {
        String token = tokenProvider.getAccessToken();
        ProducerRecord<String, ExchangeDto> record =
                new ProducerRecord<>("exchange", exchangeDto.getCurrency().name(), exchangeDto);
        record.headers().add("Authorization", ("Bearer " + token).getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(record);
    }
}
