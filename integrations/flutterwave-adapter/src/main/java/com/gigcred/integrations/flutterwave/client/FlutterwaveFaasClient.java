package com.gigcred.integrations.flutterwave.client;

import com.gigcred.integrations.flutterwave.dto.AccountRequest;
import com.gigcred.integrations.flutterwave.dto.AccountResponse;
import com.gigcred.integrations.flutterwave.dto.KycRequest;
import com.gigcred.integrations.flutterwave.dto.KycResponse;
import com.gigcred.integrations.flutterwave.dto.PayoutRequest;
import com.gigcred.integrations.flutterwave.dto.PayoutResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "flutterwaveFaasClient", url = "${flutterwave.base-url}")
public interface FlutterwaveFaasClient {

    @PostMapping(value = "/kyc/verify", consumes = MediaType.APPLICATION_JSON_VALUE)
    KycResponse startKyc(@RequestHeader("Authorization") String bearerToken, @RequestBody KycRequest request);

    @PostMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE)
    AccountResponse createAccount(@RequestHeader("Authorization") String bearerToken, @RequestBody AccountRequest request);

    @PostMapping(value = "/payouts", consumes = MediaType.APPLICATION_JSON_VALUE)
    PayoutResponse initiatePayout(
        @RequestHeader("Authorization") String bearerToken,
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody PayoutRequest request
    );
}
