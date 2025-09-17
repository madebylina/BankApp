package com.exchange.controller;

import com.exchange.model.dto.CurrencyEnum;
import com.exchange.model.dto.ExchangeDto;
import com.exchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_EXCHANGE')")
    public void setExchange(@RequestBody ExchangeDto exchangeDto) {
        exchangeService.setExchange(exchangeDto);
    }

    @GetMapping("/{currency}")
    @PreAuthorize("hasRole('ROLE_EXCHANGE')")
    public Double getExchange(@PathVariable CurrencyEnum currencyEnum) {
        return exchangeService.getExchange(currencyEnum);
    }
}
