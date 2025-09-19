package com.gigcred.integrations.flutterwave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FlutterwaveAdapterApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlutterwaveAdapterApplication.class, args);
    }
}
