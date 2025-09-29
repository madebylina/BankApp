package com.cash.contracts;

import com.blocker.controller.BlockerController;
import com.blocker.service.BlockerService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = BlockerController.class)
public class BaseContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private BlockerService blockerService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());

        Mockito.when(blockerService.validate()).thenReturn(true);
    }
}
