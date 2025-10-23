package com.front.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthenticationEventsListener {

    private final CustomMetrics customMetrics;

    @EventListener
    public void handleSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        customMetrics.incrementSucceeLogin(username);
    }

    @EventListener
    public void handleFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        customMetrics.incrementFailureLogin(username);
    }
}
