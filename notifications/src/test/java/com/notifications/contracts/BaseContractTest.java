package com.notifications.contracts;

import com.notifications.controller.NotificationsController;
import com.notifications.service.NotificationsService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = NotificationsController.class)
public abstract class BaseContractTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private NotificationsService notificationsService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(context).build());
    }
}