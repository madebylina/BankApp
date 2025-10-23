package com.generator.service;

import com.generator.dto.CurrencyEnum;
import com.generator.dto.ExchangeDto;
import com.sun.source.util.SourcePositions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class GeneratorService {

    private final ExchangeApiService exchangeApiService;
    private final ExchangeProducer exchangeProducer;
    private final List<ExchangeDto> exchangeDtoList = new ArrayList<>();

    public GeneratorService(ExchangeApiService exchangeApiService, ExchangeProducer exchangeProducer) {
        this.exchangeApiService = exchangeApiService;
        this.exchangeProducer = exchangeProducer;

        ExchangeDto rub = new ExchangeDto(CurrencyEnum.RUB, 1D);
        ExchangeDto usd = new ExchangeDto(CurrencyEnum.USD, 80D);
        ExchangeDto cny = new ExchangeDto(CurrencyEnum.CNY, 11D);
        exchangeDtoList.addAll(Arrays.asList(rub, usd, cny));
    }

    @Value("${message.text:дефолтное сообщение}")
    private String message;

    @Scheduled(fixedRate = 5000)
    public void setExchange() {
        exchangeDtoList.forEach(exchangeDto -> {
            if (!exchangeDto.getCurrency().equals(CurrencyEnum.RUB)) {
                int change = (int) (Math.random() * 3) - 1;
                if (exchangeDto.getValue() + change > 1) {
                    exchangeDto.setValue(exchangeDto.getValue() + change);
                }
            }
         //   exchangeApiService.setExchange(exchangeDto);
            exchangeProducer.setExchange(exchangeDto);
            System.out.println(exchangeDto);
        });
        System.out.println(message);
    }
}
