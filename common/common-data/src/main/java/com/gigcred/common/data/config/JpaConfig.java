package com.gigcred.common.data.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.gigcred")
@EnableJpaRepositories(basePackages = "com.gigcred")
public class JpaConfig {
}
