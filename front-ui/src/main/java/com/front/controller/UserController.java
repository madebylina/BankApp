package com.front.controller;

import com.front.dto.*;
import com.front.metrics.CustomMetrics;
import com.front.service.AccountsApiService;
import com.front.service.BankUserDetailsService;

import com.front.service.CashApiService;
import com.front.service.TransferApiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailService;
    private final AccountsApiService accountsApiService;
    private final CashApiService cashApiService;
    private final TransferApiService transferApiService;
    private final CustomMetrics customMetrics;

    /**
     * POST "/user/{login}/editPassword" - эндпоинт смены пароля (записывает список ошибок, если есть, в passwordErrors)
     * Параметры:
     *
     * @param login            login - логин пользователя
     * @param password         password - новый пароль
     * @param confirm_password confirm_password - новый пароль второй раз
     * @return редирект на "/main"
     */
    @PostMapping("/{login}/editPassword")
    public String editPassword(@PathVariable(name = "login") String login,
                               @RequestParam(name = "password") String password,
                               @RequestParam(name = "confirm_password") String confirm_password,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {
        if (!password.equals(confirm_password)) {
            redirectAttributes.addFlashAttribute("passwordErrors", List.of("Пароли не совпадают"));
            return "redirect:/main";
        }

        UserDto userDto = accountsApiService.getUserByName(login);
        userDto.setPassword(passwordEncoder.encode(password));
        userDto = accountsApiService.saveUser(userDto);
        authenticateUser(userDto.getUsername(), request);
        log.info("Пользователь {} изменил пароль", login);
        return "redirect:/main";
    }

    @PostMapping("/{login}/cash")
    public String cash(@PathVariable(name = "login") String login,
                       @RequestParam(name = "currency") CurrencyEnum currency,
                       @RequestParam(name = "value") Double value,
                       @RequestParam(name = "action") CashActionEnum action,
                       RedirectAttributes redirectAttributes) {
        UserDto userDto = accountsApiService.getUserByName(login);
        CashDto cashDto = new CashDto(userDto.getId(), currency, value, action);

        try {
            cashApiService.cash(cashDto);
            log.info("Операция с наличностью {}: {} {}", login, value, currency.name());
        } catch (RestClientResponseException restClientResponseException) {
            if (restClientResponseException.getMessage().equals("409 Conflict: \"Операция заблокирована блокировщиком\"")) {
                customMetrics.incrementCacsBlocker(login, currency.name());
            }
            redirectAttributes.addFlashAttribute("cashErrors",
                    List.of(restClientResponseException.getResponseBodyAsString()));
        }
        return "redirect:/main";
    }

    @PostMapping("/{login}/transfer")
    public String transfer(@PathVariable(name = "login") String login,
                           @RequestParam(name = "from_currency") CurrencyEnum fromCurrency,
                           @RequestParam(name = "to_currency") CurrencyEnum toCurrency,
                           @RequestParam(name = "value") Double value,
                           @RequestParam(name = "to_login") String toLogin,
                           RedirectAttributes redirectAttributes) {

        List<String> transferErrors = new ArrayList<>();
        List<String> transferOtherErrors = new ArrayList<>();

        UserDto fromUserDto = accountsApiService.getUserByName(login);
        UserDto toUserDto = accountsApiService.getUserByName(toLogin);
        ExchangeDto fromExchangeDto = new ExchangeDto(fromCurrency, null);
        ExchangeDto toExchangeDto = new ExchangeDto(toCurrency, null);
        TransferDto transferDto = new TransferDto(fromUserDto.getId(), fromExchangeDto, toExchangeDto, value, toUserDto.getId());

        if (login.equals(toLogin) && fromCurrency.equals(toCurrency)) {
            customMetrics.incrementFailureTransfer(login, fromCurrency.name(), toLogin, toCurrency.name());
            redirectAttributes.addFlashAttribute("transferErrors", List.of("Перевести можно только между разными счетами"));
            return "redirect:/main";
        }

        try {
            transferApiService.transfer(transferDto);
            log.info("Операция перевода от {}: {} {} к {}: {}", login, value, fromCurrency.name(), toLogin, toCurrency.name());
        } catch (RestClientResponseException restClientResponseException) {
            if (login.equals(toLogin)) {
                if (restClientResponseException.getMessage().equals("409 Conflict: \"Операция заблокирована блокировщиком\"")) {
                    customMetrics.incrementTransferBlocker(login, fromCurrency.name(), toLogin, toCurrency.name());
                }
                transferErrors.add(restClientResponseException.getResponseBodyAsString().formatted(toLogin));
            } else {
                transferErrors.add(restClientResponseException.getResponseBodyAsString().formatted(toLogin));
            }
        }
        if (!transferErrors.isEmpty() || !transferOtherErrors.isEmpty()) {
            customMetrics.incrementFailureTransfer(login, fromCurrency.name(), toLogin, toCurrency.name());
        }
        redirectAttributes.addFlashAttribute("transferErrors", transferErrors);
        redirectAttributes.addFlashAttribute("transferOtherErrors", transferOtherErrors);
        return "redirect:/main";
    }

    @PostMapping("/{login}/editUserAccounts")
    public String editUserAccounts(@PathVariable(name = "login") String login,
                                   @RequestParam(name = "name") String name,
                                   @RequestParam(name = "birthdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate,
                                   @RequestParam(name = "account", required = false) List<String> selectedCurrencies,
                                   RedirectAttributes redirectAttributes) {

        List<String> userAccountsErrors = new ArrayList<>();

        UserDto userDto = accountsApiService.getUserByName(login);

        if (Period.between(birthdate, LocalDate.now()).getYears() < 18) {
            userAccountsErrors.add("Вам должно быть больше 18 лет");
        } else {
            userDto.setPersonName(name);
            userDto.setDateOfBirth(birthdate);
            accountsApiService.saveUser(userDto);
        }

        Arrays.stream(CurrencyEnum.values())
                .forEach(currency -> {
                    AccountDto accountDto = accountsApiService.getAccountByUserAndCurrency(userDto.getId(), currency);
                    if (accountDto.getExists()) {
                        if (Objects.isNull(selectedCurrencies) || !selectedCurrencies.contains(accountDto.getCurrency().name())) {
                            if (accountDto.getValue() == 0) {
                                accountsApiService.deleteAccount(accountDto);
                            } else {
                                userAccountsErrors.add("Баланс на счету %s не равен 0".formatted(currency.getTitle()));
                            }
                        }
                    } else {
                        if (!Objects.isNull(selectedCurrencies) && selectedCurrencies.contains(accountDto.getCurrency().name())) {
                            accountDto.setExists(true);
                            accountsApiService.saveAccount(accountDto);
                        }
                    }
                });

        redirectAttributes.addFlashAttribute("passwordErrors", userAccountsErrors);
        return "redirect:/main";
    }

    private void authenticateUser(String username, HttpServletRequest request) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }
}
