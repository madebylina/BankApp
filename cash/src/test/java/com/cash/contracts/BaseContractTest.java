package com.cash.contracts;

import com.cash.controller.CashController;
import com.cash.dto.UserDto;
import com.cash.service.AccountsApiService;
import com.cash.service.BlockerApiService;
import com.cash.service.NotificationsApiService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = CashController.class, excludeAutoConfiguration = OAuth2ClientAutoConfiguration.class)
public abstract class BaseContractTest {

    @Autowired
    protected WebApplicationContext context;

    @MockitoBean
    protected AccountsApiService accountsApiService;

    @MockitoBean
    protected NotificationsApiService notificationsApiService;

    @MockitoBean
    protected BlockerApiService blockerApiService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());

        Mockito.when(accountsApiService.getUserById(Mockito.anyLong())).thenReturn(new UserDto());

        Mockito.when(blockerApiService.validate()).thenReturn(true);
    }

}