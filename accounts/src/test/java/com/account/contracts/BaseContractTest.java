package com.account.contracts;

import com.controller.AccountController;
import com.model.dto.AccountDto;
import com.model.dto.CurrencyEnum;
import com.model.dto.TransferDto;
import com.model.dto.UserDto;
import com.AccountService;
import com.NotificationsApiService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.management.OperationsException;
import javax.security.auth.login.AccountNotFoundException;
import java.time.LocalDate;
import java.util.List;

@WebMvcTest(controllers = AccountController.class, excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class)
public abstract class BaseContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private NotificationsApiService notificationsApiService;

    @BeforeEach
    public void setup() throws AccountNotFoundException, OperationsException {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());

        Mockito.when(accountService.getAccountsByUserIdAndCurrency(1L, CurrencyEnum.USD))
                .thenReturn(new AccountDto(100L, 1L, CurrencyEnum.USD, true, 1500.75));

        Mockito.when(accountService.saveUser(Mockito.any(UserDto.class)))
                .thenAnswer(invocation -> {
                    UserDto input = invocation.getArgument(0);
                    return new UserDto(10L, input.getUsername(), "hashed_" +
                            input.getPassword(), input.getPersonName(), input.getDateOfBirth());
                });

        Mockito.when(accountService.getUserById(1L)).
                thenReturn(new UserDto(1L, "user1", "hashed_password_1", "Иван Иванов",
                        LocalDate.parse("1990-01-01")));

        Mockito.when(accountService.getUserByUsername("user1"))
                .thenReturn(new UserDto(1L, "user1", "hashed_password_1", "Иван Иванов",
                        LocalDate.parse("1990-01-01")));

        Mockito.when(accountService.getUsers()).thenReturn(List.of(
                new UserDto(1L, "user1", "hashed_password_1", "Иван Иванов",
                        LocalDate.parse("1990-01-01")),
                new UserDto(2L, "user2", "hashed_password_2", "Петр Петров",
                        LocalDate.parse("1985-05-15")))
        );

        Mockito.when(accountService.saveAccount(Mockito.any(AccountDto.class)))
                .thenAnswer(invocation -> {
                    AccountDto input = invocation.getArgument(0);
                    return new AccountDto(101L, input.getUserId(), input.getCurrency(), input.getExists(),
                            input.getValue());
                });

        Mockito.when(accountService.getAccountsByUserIdAndCurrency(2L, CurrencyEnum.USD))
                .thenReturn(new AccountDto(101L, 1L, CurrencyEnum.USD, true, 1000.00));

        Mockito.doNothing().when(accountService).transfer(Mockito.any(TransferDto.class));

    }
}