package com.gigcred.integrations.flutterwave.dto;

public record PayoutRequest(
    String sourceCurrency,
    String destinationCurrency,
    long amount,
    String beneficiaryAccount,
    String beneficiaryCountry,
    String reference
) {
}
