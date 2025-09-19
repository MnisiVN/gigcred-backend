package com.gigcred.integrations.flutterwave.dto;

public record AccountRequest(
    String userId,
    String currency,
    String productType
) {
}
