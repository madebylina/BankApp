package com.front.controller;

import com.front.dto.RateDto;
import com.front.service.TransferApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final TransferApiService transferApiService;

    /**
     * GET "/api/rates" - эндпоинт получения курсов валют
     *
     * @return JSON со списком курсов валют:
     * title - название валюты
     * name - код валюты
     * value - курс валюты по отношению к рублю (для рубля 1)
     */
    @GetMapping("/rates")
    public List<RateDto> getRates() {
        return transferApiService.getExchangeDtoList().stream().map(exchangeDto -> {
            return new RateDto(exchangeDto.getCurrency().getTitle(), exchangeDto.getCurrency().name(), exchangeDto.getValue());
        }).toList();
    }

}
