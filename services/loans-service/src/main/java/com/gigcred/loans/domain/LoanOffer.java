package com.gigcred.loans.domain;

import java.time.Instant;

public record LoanOffer(String offerId, String userId, long principalCents, double feePct, int tenorDays, Instant expiresAt) {
}
