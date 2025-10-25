package com.cash.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/actuator/*").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(customizer -> customizer
                        .jwt(jwtCustomizer -> {
                            JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                            jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
                                Map<String, Object> realmAccess = jwt.getClaim("realm_access");
                                List<String> roles = (List<String>) realmAccess.get("roles");
                                return roles.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .map(GrantedAuthority.class::cast)
                                        .toList();
                            });
                            jwtCustomizer.jwtAuthenticationConverter(jwtAuthenticationConverter);

                        })
                )
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);

        manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .refreshToken()
                .build());

        return manager;
    }

 /*   @Bean
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
    }*/

    @Bean
    public ClientHttpRequestInterceptor authInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return (request, body, execution) -> {
            var authRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("keycloak")
                    .principal("system")
                    .build();

            var client = authorizedClientManager.authorize(authRequest);
            if (client == null) {
                throw new IllegalStateException("Не удалось получить OAuth2AuthorizedClient");
            }

            request.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());
            return execution.execute(request, body);
        };
    }

    @Bean
    public RestClient accountsApiClient(RestClient.Builder builder, ClientHttpRequestInterceptor authInterceptor,
                                        @Value("${appservices.accounts-api:http://localhost:8081/api}") String baseUrl) {
        return builder
                .requestInterceptor(authInterceptor) // добавляем токен
                .baseUrl(baseUrl)
                .build(); // трассировка добавится автоматически
    }

    @Bean
    public RestClient blockerApiClient(RestClient.Builder builder, ClientHttpRequestInterceptor authInterceptor,
                                       @Value("${appservices.blocker-api:http://localhost:8087/api}") String baseUrl) {
        return builder
                .requestInterceptor(authInterceptor)
                .baseUrl(baseUrl)
                .build();
    }
}