package com.cash.controller;

import com.cash.dto.CashDto;
import com.cash.dto.NotificationDto;
import com.cash.dto.UserDto;
import com.cash.service.AccountsApiService;
import com.cash.service.BlockerApiService;
import com.cash.service.NotificationsApiService;
import com.cash.service.NotificationsProducer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.OperationsException;
import java.util.Enumeration;

@RestController
@RequestMapping("/api/cash")
@RequiredArgsConstructor
public class CashController {

    private final AccountsApiService accountsApiService;
    private final NotificationsApiService notificationApiService;
    private final NotificationsProducer notificationsProducer;
    private final BlockerApiService blockerApiService;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_CASH')")
    public void cash(@RequestBody CashDto cashDto, HttpServletRequest request) throws OperationsException {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            System.out.println(name + ": " + request.getHeader(name));
        }

        if (!blockerApiService.validate()) {
            throw new OperationsException("Операция заблокирована блокировщиком");
        }
        UserDto userDto = accountsApiService.getUserById(cashDto.getUserId());
        notificationsProducer.notificate(new NotificationDto(userDto.getUsername(), cashDto.toString()));
        accountsApiService.cash(cashDto);
    }

    @ExceptionHandler(OperationsException.class)
    public ResponseEntity<String> handleOperationsException(OperationsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
