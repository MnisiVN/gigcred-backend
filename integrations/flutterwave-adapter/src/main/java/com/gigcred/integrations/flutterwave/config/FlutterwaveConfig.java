package com.gigcred.integrations.flutterwave.config;

import feign.Logger;
import feign.Request;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FlutterwaveProperties.class)
public class FlutterwaveConfig {

    @Bean
    public Request.Options flutterwaveRequestOptions(FlutterwaveProperties properties) {
        return new Request.Options(properties.connectTimeoutMs(), properties.readTimeoutMs(), true);
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
