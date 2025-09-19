package com.gigcred.integrations.flutterwave;

import com.gigcred.integrations.flutterwave.client.FlutterwaveFaasClient;
import com.gigcred.integrations.flutterwave.config.FlutterwaveProperties;
import com.gigcred.integrations.flutterwave.dto.AccountRequest;
import com.gigcred.integrations.flutterwave.dto.AccountResponse;
import com.gigcred.integrations.flutterwave.dto.KycRequest;
import com.gigcred.integrations.flutterwave.dto.KycResponse;
import com.gigcred.integrations.flutterwave.dto.PayoutRequest;
import com.gigcred.integrations.flutterwave.dto.PayoutResponse;
import org.springframework.stereotype.Component;

@Component
public class FlutterwaveClientFacade {

    private final FlutterwaveFaasClient client;
    private final FlutterwaveProperties properties;

    public FlutterwaveClientFacade(FlutterwaveFaasClient client, FlutterwaveProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    public KycResponse startKyc(KycRequest request) {
        return client.startKyc(authHeader(), request);
    }

    public AccountResponse createAccount(AccountRequest request) {
        return client.createAccount(authHeader(), request);
    }

    public PayoutResponse initiatePayout(String idempotencyKey, PayoutRequest request) {
        return client.initiatePayout(authHeader(), idempotencyKey, request);
    }

    private String authHeader() {
        return "Bearer " + properties.apiKey();
    }
}
