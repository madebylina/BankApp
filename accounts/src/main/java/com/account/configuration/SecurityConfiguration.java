package com.account.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    /**
     * Настраивает цепочку фильтров безопасности Spring Security.
     * - Все HTTP-запросы требуют аутентификации.
     * - Включается поддержка OAuth2 Resource Server с проверкой JWT токенов.
     * - Настраивается конвертер JWT для извлечения ролей из claim "realm_access.roles"
     *   и преобразования их в GrantedAuthority для Spring Security.
     *
     * @param security объект конфигурации HttpSecurity
     * @return настроенный объект SecurityFilterChain
     * @throws Exception при ошибках конфигурации безопасности
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .authorizeHttpRequests(requests -> requests
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

    /**
     * Создаёт и настраивает {@link OAuth2AuthorizedClientManager}, который управляет
     * авторизованными OAuth2 клиентами и их токенами.
     *
     * <p>Менеджер использует предоставленные {@link ClientRegistrationRepository} для хранения
     * регистрации клиентов OAuth2 и {@link OAuth2AuthorizedClientService} для управления
     * авторизованными клиентами.</p>
     *
     * <p>Настроенный провайдер авторизации позволяет получать токены с помощью
     * grant type "client_credentials" и автоматически обновлять их с помощью refresh token.</p>
     *
     * @param clientRegistrationRepository репозиторий настроек OAuth2 клиентов
     * @param authorizedClientService сервис для сохранения и загрузки авторизованных клиентов
     * @return настроенный {@link OAuth2AuthorizedClientManager}
     */
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

    /**
     * Создаёт бин RestClient.Builder с поддержкой балансировки нагрузки (@LoadBalanced).
     * Добавляет в каждый исходящий HTTP-запрос интерсептор, который:
     * - Создаёт запрос авторизации OAuth2 с clientRegistrationId "keycloack" и principal "system".
     * - Получает OAuth2AuthorizedClient через authorizedClientManager, то есть использует клиентские учетные данные для авторизации.
     * - Если клиент не получен, выбрасывает исключение.
     * - Добавляет полученный OAuth2 Access Token в заголовок Authorization в виде Bearer токена.
     * Таким образом, все запросы, отправленные через этот клиент, автоматически содержат актуальный OAuth2 токен для аутентификации.
     *
     * @param authorizedClientManager менеджер, отвечающий за получение и обновление OAuth2 токенов
     * @return настроенный RestClient.Builder с автоподстановкой OAuth2 токена
     */
    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder(OAuth2AuthorizedClientManager authorizedClientManager) {
        return RestClient.builder()
                .requestInterceptor(((request, body, execution) -> {
                    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                            .withClientRegistrationId("keycloack")
                            .principal("system")
                            .build();

                    OAuth2AuthorizedClient client = authorizedClientManager.authorize(authorizeRequest);
                    if (client == null) {
                        throw  new IllegalStateException("Не удалось получить OAuth2AuthorizedClient");
                    }

                    request.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());
                    return execution.execute(request, body);
                }));
    }
}
