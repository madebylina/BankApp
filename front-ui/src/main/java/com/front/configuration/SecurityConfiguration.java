package com.front.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

@Configuration
public class SecurityConfiguration {

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);

        manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials() // Включаем получение токена с помощью client_credentials
                .refreshToken() // Также включаем использование refresh_token
                .build());

        return manager;
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(OAuth2AuthorizedClientManager authorizedClientManager) {

        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    OAuth2AuthorizeRequest authRequest = OAuth2AuthorizeRequest
                            .withClientRegistrationId("keycloak")
                            .principal("system")
                            .build();

                    OAuth2AuthorizedClient client = authorizedClientManager.authorize(authRequest);
                    if (client == null) {
                        throw new IllegalStateException("Не удалось получить OAuth2AuthorizedClient");
                    }

                    request.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());
                    return execution.execute(request, body);
                });
    }



    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeHttpRequests(requests -> requests
                        .anyRequest().permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}