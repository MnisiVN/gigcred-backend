package com.gigcred.integrations.flutterwave.dto;

import java.time.Instant;

public record PayoutResponse(
    String status,
    String reference,
    Instant createdAt,
    String message
) {
}
