package com.gigcred.payments.config;

import com.gigcred.common.domain.IdempotencyService;
import com.gigcred.common.domain.InMemoryIdempotencyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentsConfig {

    @Bean
    public IdempotencyService idempotencyService() {
        return new InMemoryIdempotencyService();
    }
}
