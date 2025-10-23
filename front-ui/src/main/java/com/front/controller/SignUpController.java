package com.front.controller;

import com.front.dto.UserDto;
import com.front.service.AccountsApiService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/signup")
public class SignUpController {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AccountsApiService accountsApiService;

    @GetMapping("")
    public String createAccount() {
        return "signup";
    }

    /**
     * POST "/signup" - эндпоинт регистрации нового пользователя
     * Параметры:
     *
     * @param model
     * @param request
     * @param login
     * @param password
     * @param confirm_password
     * @param name
     * @param birthdate
     * @return Возвращает:
     * редирект на "/main"
     * В случае ошибок возвращает:
     * шаблон "signup.html"
     * используется модель для заполнения шаблона:
     * "login" - строка с логином пользователя
     * "name" - строка с фамилией и именем пользователя
     * "birthdate" - LocalDate с датой рождения пользователя
     * "accounts" - список всех зарегистрированных пользователей
     * "errors" - список ошибок при регистрации
     */
    @PostMapping("")
    public String createAccount(Model model, HttpServletRequest request,
                                @RequestParam(name = "login") String login,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "confirm_password") String confirm_password,
                                @RequestParam(name = "name") String name,
                                @RequestParam(name = "birthdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthdate) {

        List<String> errors = new ArrayList<>();
        if (!password.equals(confirm_password)) {
            errors.add("Пароли не совпадают");
        }
        if (Period.between(birthdate, LocalDate.now()).getYears() < 18) {
            errors.add("Вам должно быть больше 18 лет");
        }

        if (errors.isEmpty()) {
            try {
                accountsApiService.saveUser(new UserDto(null, login, passwordEncoder.encode(password), name, birthdate));
            } catch (HttpClientErrorException httpClientErrorException) {
                if (httpClientErrorException.getStatusCode().equals(HttpStatus.CONFLICT)) {
                    errors.add("Пользователь с таким именем уже существует");
                } else {
                    errors.add(httpClientErrorException.getMessage());
                }

            } catch (Exception e) {
                errors.add(e.getMessage());
            }
        }
        if (!errors.isEmpty()) {
            model.addAttribute("login", login);
            model.addAttribute("name", name);
            model.addAttribute("birthdate", birthdate);
            model.addAttribute("errors", errors);
            return "signup";
        }
        authenticateUser(login, request);
        log.info("Создан новый аккаунт {}: ", login);
        return "redirect:/main";
    }

    private void authenticateUser(String username, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
    }
}
