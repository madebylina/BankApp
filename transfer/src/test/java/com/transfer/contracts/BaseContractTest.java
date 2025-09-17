package com.transfer.contracts;

import com.transfer.controller.TransferController;
import com.transfer.dto.CurrencyEnum;
import com.transfer.dto.UserDto;
import com.transfer.service.AccountsApiService;
import com.transfer.service.BlockerApiService;
import com.transfer.service.ExchangeApiService;
import com.transfer.service.NotificationsApiService;
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
import java.time.LocalDate;

@WebMvcTest(controllers = TransferController.class, excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class)
public abstract class BaseContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private AccountsApiService accountsApiService;

    @MockitoBean
    private ExchangeApiService exchangeApiService;

    @MockitoBean
    private NotificationsApiService notificationsApiService;

    @MockitoBean
    private BlockerApiService blockerApiService;

    @BeforeEach
    public void setup() throws OperationsException {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());

        Mockito.when(blockerApiService.validate()).thenReturn(true);

        Mockito.when(accountsApiService.getUserById(Mockito.anyLong()))
                .thenReturn(new UserDto(1L, "user123", "pass", "Иван Иванов",
                        LocalDate.of(1990, 1, 1)));

        Mockito.doNothing().when(notificationsApiService).notificate(Mockito.any());

        Mockito.when(exchangeApiService.getExchangeValue(CurrencyEnum.RUB)).thenReturn(75.0);
        Mockito.when(exchangeApiService.getExchangeValue(CurrencyEnum.USD)).thenReturn(1.0);
        Mockito.when(exchangeApiService.getExchangeValue(CurrencyEnum.CNY)).thenReturn(11.0);

        Mockito.doNothing().when(accountsApiService).transfer(Mockito.any());
    }
}
