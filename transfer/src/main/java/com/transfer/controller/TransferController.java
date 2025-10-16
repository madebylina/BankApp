package com.transfer.controller;

import com.transfer.dto.CurrencyEnum;
import com.transfer.dto.ExchangeDto;
import com.transfer.dto.NotificationDto;
import com.transfer.dto.TransferDto;
import com.transfer.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.OperationsException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {

    private final AccountsApiService accountsApiService;
    private final ExchangeApiService exchangeApiService;
    private final NotificationsApiService notificationsApiService;
    private final NotificationsProducer notificationsProducer;
    private final BlockerApiService blokerApiService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_TRANSFER')")
    public void transfer(@RequestBody TransferDto transferDto) throws OperationsException {
        if (!blokerApiService.validate()) {
            throw new OperationsException("Операция заблокирована блокировщиком");
        }
        NotificationDto notificationDto = new NotificationDto(accountsApiService.getUserById(transferDto.getUserId())
                .getUsername(), transferDto.toString());
      //  notificationsApiService.notificate(notificationDto);
        notificationsProducer.notificate(notificationDto);
        Double fromCurrencyValue = exchangeApiService.getExchangeValue(transferDto.getFromExchange().getCurrency());
        Double toCurrencyValue = exchangeApiService.getExchangeValue(transferDto.getToExchange().getCurrency());
        transferDto.getFromExchange().setValue(fromCurrencyValue);
        transferDto.getToExchange().setValue(toCurrencyValue);
        accountsApiService.transfer(transferDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_TRANSFER')")
    public List<ExchangeDto> getExchangeList() throws OperationsException {
        return Arrays.stream(CurrencyEnum.values()).map(currencyEnum ->
                new ExchangeDto(currencyEnum, exchangeApiService.getExchangeValue(currencyEnum)))
                .collect(Collectors.toList());
    }

    @ExceptionHandler(OperationsException.class)
    public ResponseEntity<String> handleOperationsException(OperationsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
