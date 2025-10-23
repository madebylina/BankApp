package com.generator.configuration;

import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.stereotype.Component;

@Component
public class OAuth2TokenProvider {

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public OAuth2TokenProvider(OAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientManager = authorizedClientManager;
    }

    public String getAccessToken() {
        OAuth2AuthorizeRequest authRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak")
                .principal("system")
                .build();

        OAuth2AuthorizedClient client = authorizedClientManager.authorize(authRequest);
        if (client == null) {
            throw new IllegalStateException("Не удалось получить OAuth2 токен");
        }

        return client.getAccessToken().getTokenValue();
    }
}