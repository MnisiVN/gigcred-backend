package com.gigcred.integrations.flutterwave.dto;

public record AccountResponse(
    String status,
    String accountId,
    String accountNumber,
    String bankName
) {
}
