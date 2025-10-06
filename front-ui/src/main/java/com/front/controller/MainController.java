package com.front.controller;

import com.front.dto.AccountDto;
import com.front.dto.CurrencyEnum;
import com.front.dto.UserDto;
import com.front.service.AccountsApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {

    private final AccountsApiService accountsApiService;

    /**
     * GET "/main" - главная страница
     *
     * @param model
     * @param principal
     * @return шаблон "main.html"
     * используется модель для заполнения шаблона:
     * "login" - строка с логином пользователя
     * "name" - строка с фамилией и именем пользователя
     * "birthdate" - LocalDate с датой рождения пользователя
     * "accounts" - список всех зарегистрированных пользователей:
     * "currency" - enum валюта:
     * "title" - название валюты
     * "name()" - код валюты
     * "value" - сумма на счету пользователя в этой валюте
     * "exists" - true, если у пользователя есть счет в этой валюте, false, если нет
     * "currency" - список всех доступных валют:
     * "title" - название валюты
     * "name()" - код валюты
     * "users" - список всех пользователей:
     * "login" - логин пользователя
     * "name" - фамилия и имя пользователя
     * "passwordErrors" - список ошибок при смене пароля (null, если не выполнялась смена пароля)
     * "userAccountsErrors" - список ошибок при редактировании настроек аккаунта (null, если не выполнялось редактирование)
     * "cashErrors" - список ошибок при внесении/снятии денег (null, если не выполнялось внесение/снятие)
     * "transferErrors" - список ошибок при переводе между своими счетами (null, если не выполнялся перевод)
     * "transferOtherErrors" - список ошибок при переводе на счет другого пользователя (null, если не выполнялся перевод)
     */
    @GetMapping("")
    public String getMain(Model model, Principal principal) {
        if (Objects.isNull(principal)) {
            return "main";
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDto userDto = accountsApiService.getUserByName(username);

        model.addAttribute("login", userDto.getUsername());
        model.addAttribute("name", userDto.getPersonName());
        model.addAttribute("birthdate", userDto.getDateOfBirth());

        List<AccountDto> accountDtoList = Arrays.stream(CurrencyEnum.values())
                .map(currency -> {
                    return accountsApiService.getAccountByUserAndCurrency(userDto.getId(), currency);
                }).collect(Collectors.toList());
        model.addAttribute("accounts", accountDtoList);
        model.addAttribute("currency", CurrencyEnum.values());
        model.addAttribute("users", accountsApiService.getUsers());
        return "main";
    }

}
