package com.exchange.service;

import com.exchange.model.dto.CurrencyEnum;
import com.exchange.model.dto.ExchangeDto;
import com.exchange.model.entities.Exchange;
import com.exchange.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;

    public void setExchange(ExchangeDto exchangeDto) {
        Exchange exchange = exchangeRepository.findByCurrency(exchangeDto.getCurrency());
        if(Objects.isNull(exchange)) {
            exchange = new Exchange();
            exchange.setCurrency(exchangeDto.getCurrency());
        }
        exchange.setValue(exchangeDto.getValue());
        exchangeRepository.save(exchange);
    }

    public Double getExchange(CurrencyEnum currency) {
        Exchange exchange = exchangeRepository.findByCurrency(currency);
        return exchange.getValue();
    }
}
