package com.front.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CustomMetrics {

    private final MeterRegistry meterRegistry;

    public void incrementSucceeLogin(String username) {
        meterRegistry.counter("user_login_success_total", "username", username).increment();
    }

    public void incrementFailureLogin(String username) {
        meterRegistry.counter("user_login_failure_total", "username", username).increment();
    }

    public void incrementFailureTransfer(String sender, String senderAccount, String recipient, String recipientAccount) {
        meterRegistry.counter("transfer_failure_total", "sender", sender, "sender_account", senderAccount,
                "recipient", recipient, "recipient_account", recipientAccount).increment();
    }

    public void incrementTransferBlocker(String sender, String senderAccount, String recipient, String recipientAccount) {
        meterRegistry.counter("transfer_blocker_total", "sender", sender, "sender_account", senderAccount,
                "recipient", recipient, "recipient_account", recipientAccount).increment();
    }

    public void incrementCacsBlocker(String username, String userAccount) {
        meterRegistry.counter("cash_blocker_total", "username", username, "user_account", userAccount).increment();
    }

}
