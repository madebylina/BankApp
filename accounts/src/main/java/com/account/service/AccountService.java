package com.account.service;

import com.account.mapper.AccountMapper;
import com.account.mapper.UserMapper;
import com.account.model.dto.AccountDto;
import com.account.model.dto.CurrencyEnum;
import com.account.model.dto.TransferDto;
import com.account.model.dto.UserDto;
import com.account.model.entities.Account;
import com.account.model.entities.User;
import com.account.repository.AccountRepository;
import com.account.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.OperationsException;
import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public List<UserDto> getUsers() {

        return userRepository.findAll().stream().map(user -> {
            UserDto userDto = new UserDto();
            userDto.setUsername(user.getUsername());
            userDto.setPersonName(user.getPersonName());
            return userDto;
        }).collect(Collectors.toList());
    }

    public UserDto getUserByUsername(String username) throws AccountNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AccountNotFoundException("User not found: " + username));
        return UserMapper.toUserDto(user);
    }

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElse(new User());
        return UserMapper.toUserDto(user);
    }

    public UserDto saveUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    public AccountDto getAccountsByUserIdAndCurrency(Long userId, CurrencyEnum currency) {
        return accountRepository.findByUserIdAndCurrency(userId, currency)
                .map(AccountMapper::toAccountDto)
                .orElse(new AccountDto(null, userId, currency, false, 0.0));
    }

    public AccountDto saveAccount(AccountDto accountDto) {
        Account account = accountRepository.save(AccountMapper.toAccount(accountDto));
        return AccountMapper.toAccountDto(account);
    }

    public void deleteAccountById(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    @Transactional
    public void transfer(TransferDto transferDto) throws OperationsException {
        AccountDto fromAccountDto = getAccountsByUserIdAndCurrency(
                transferDto.getUserId(),
                transferDto.getFromExchange().getCurrency()
        );

        BigDecimal fromValue = BigDecimal.valueOf(fromAccountDto.getValue()).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal transferValue = BigDecimal.valueOf(transferDto.getValue()).setScale(2, RoundingMode.HALF_EVEN);

        if (fromValue.compareTo(transferValue) < 0) {
            throw new OperationsException("На счёте недостаточно средств");
        }

        // Вычитаем сумму перевода с учётом точности
        fromValue = fromValue.subtract(transferValue);
        fromAccountDto.setValue(fromValue.doubleValue());
        accountRepository.save(AccountMapper.toAccount(fromAccountDto));

        // Получаем счёт получателя
        AccountDto toAccountDto = getAccountsByUserIdAndCurrency(
                transferDto.getToUserId(),
                transferDto.getToExchange().getCurrency()
        );

        if (toAccountDto.getId() == null) {
            throw new OperationsException(
                    String.format("У пользователя %s нет счёта в нужной валюте", transferDto.getToUserId())
            );
        }

        // Конвертация с учётом курсов валют - используем BigDecimal для точности
        BigDecimal rubValue = transferValue.multiply(BigDecimal.valueOf(transferDto.getFromExchange().getValue()));
        BigDecimal currencyValue = rubValue.divide(BigDecimal.valueOf(transferDto.getToExchange().getValue()), 2, RoundingMode.HALF_EVEN);

        // Добавляем конвертированную сумму на счёт получателя
        BigDecimal toValue = BigDecimal.valueOf(toAccountDto.getValue()).add(currencyValue);

        toAccountDto.setValue(toValue.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
        accountRepository.save(AccountMapper.toAccount(toAccountDto));
    }

}
