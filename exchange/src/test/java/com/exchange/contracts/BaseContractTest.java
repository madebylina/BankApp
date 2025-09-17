package com.exchange.contracts;

import com.exchange.controller.ExchangeController;
import com.exchange.model.dto.CurrencyEnum;
import com.exchange.service.ExchangeService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = ExchangeController.class)
public class BaseContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ExchangeService exchangeService;

    @BeforeEach
    public void setuo() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
        Mockito.when(exchangeService.getExchange(CurrencyEnum.USD)).thenReturn(75.0);
    }
}
