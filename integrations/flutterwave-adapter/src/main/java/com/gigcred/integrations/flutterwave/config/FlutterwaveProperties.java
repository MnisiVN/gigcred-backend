package com.gigcred.integrations.flutterwave.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flutterwave")
public record FlutterwaveProperties(
    String baseUrl,
    String apiKey,
    String webhookSecret,
    int connectTimeoutMs,
    int readTimeoutMs
) {
}
