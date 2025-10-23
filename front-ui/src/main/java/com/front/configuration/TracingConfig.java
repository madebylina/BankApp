package com.front.configuration;

import io.micrometer.tracing.SamplerFunction;
import io.micrometer.tracing.http.HttpRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class TracingConfig {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Bean
    public SamplerFunction<HttpRequest> httpSampler() {
        return request -> {
            String path = request.path();
            if (path.startsWith("/api/rates")) {
                return counter.incrementAndGet() % 10 == 0;
            }
            return true;
        };
    }

}
