package com.cash.controller;

import com.cash.dto.CashDto;
import com.cash.dto.NotificationDto;
import com.cash.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.OperationsException;

@RestController
@RequestMapping("/api/cash")
@RequiredArgsConstructor
public class CashController {

    private final AccountsApiService accountsApiService;
    private final NotificationApiService notificationApiService;
    private final BlockerApiService blockerApiService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_CASH')")
    public void cash(@RequestBody CashDto cashDto) throws OperationsException {
        if (!blockerApiService.validate()) {
            throw new OperationsException("Операция заблокирована блокировщиком");
        }
        UserDto userDto = accountsApiService.getUserByid(cashDto.getUserId());
        notificationApiService.notificate(new NotificationDto(userDto.getUsername(), cashDto.toString()));
        accountsApiService.cash(cashDto);
    }

    @ExceptionHandler(OperationsException.class)
    public ResponseEntity<String> handleOperationsException(OperationsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
