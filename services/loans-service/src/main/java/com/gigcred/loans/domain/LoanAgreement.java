package com.gigcred.loans.domain;

import java.time.Instant;
import java.util.UUID;

public class LoanAgreement {
    private final String id;
    private final String offerId;
    private final String userId;
    private final long principalCents;
    private final double feePct;
    private final int tenorDays;
    private final Instant createdAt;
    private String status;

    public LoanAgreement(String offerId, String userId, long principalCents, double feePct, int tenorDays) {
        this.id = UUID.randomUUID().toString();
        this.offerId = offerId;
        this.userId = userId;
        this.principalCents = principalCents;
        this.feePct = feePct;
        this.tenorDays = tenorDays;
        this.createdAt = Instant.now();
        this.status = "ACTIVE";
    }

    public String getId() {
        return id;
    }

    public String getOfferId() {
        return offerId;
    }

    public String getUserId() {
        return userId;
    }

    public long getPrincipalCents() {
        return principalCents;
    }

    public double getFeePct() {
        return feePct;
    }

    public int getTenorDays() {
        return tenorDays;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void markRepaid() {
        this.status = "REPAID";
    }
}
