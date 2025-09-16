package com.controller;

import com.AccountService;
import com.NotificationsApiService;
import com.model.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.management.OperationsException;
import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;
    private final NotificationsApiService notificationsApiService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public List<UserDto> getUsers() throws AccountNotFoundException {
        return accountService.getUsers();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public UserDto getUserByName(@RequestParam String username) throws AccountNotFoundException {
        return accountService.getUserByUsername(username);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public UserDto getUserById(@PathVariable Long userId) {
        return accountService.getUserById(userId);
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public UserDto saveUser(@RequestBody UserDto userDto) {
        notificationsApiService.notificate(new NotificationDto(userDto.getUsername(), userDto.toString()));
        return accountService.saveUser(userDto);
    }

    @GetMapping("/value/{userId}/{currency}")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public AccountDto getAccountByUserAndCurrency(@PathVariable Long userId,
                                                  @PathVariable CurrencyEnum currency) {
        return accountService.getAccountsByUserIdAndCurrency(userId, currency);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public AccountDto saveAccount(@RequestBody AccountDto accountDto) {
        NotificationDto notificationDto = new NotificationDto(accountService.getUserById(accountDto.getUserId())
                .getUsername(), accountDto.toString());
        notificationsApiService.notificate(notificationDto);

        return accountService.saveAccount(accountDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccountById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cash")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public ResponseEntity<Void> cash(@RequestBody CashDto cashDto) throws OperationsException {
        AccountDto accountDto = accountService.getAccountsByUserIdAndCurrency(cashDto.getUserId(), cashDto.getCurrency());

        BigDecimal currentValue = BigDecimal.valueOf(accountDto.getValue()).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal cashValue = BigDecimal.valueOf(cashDto.getValue()).setScale(2, RoundingMode.HALF_EVEN);

        if (cashDto.getCashAction() == CashActionEnum.GET) {
            if (currentValue.compareTo(cashValue) < 0) {
                throw new OperationsException("На счёте не достаточно средств");
            }
            accountDto.setValue(currentValue.subtract(cashValue).doubleValue());
        } else if (cashDto.getCashAction() == CashActionEnum.PUT) {
            accountDto.setExists(true);
            accountDto.setValue(currentValue.add(cashValue).doubleValue());
        }

        accountService.saveAccount(accountDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('ROLE_ACCOUNT')")
    public ResponseEntity<Void> transfer(@RequestBody TransferDto transferDto) throws OperationsException {
        accountService.transfer(transferDto);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(OperationsException.class)
    public ResponseEntity<String> handleOperationsException(OperationsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleUserAlreadyExist(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
