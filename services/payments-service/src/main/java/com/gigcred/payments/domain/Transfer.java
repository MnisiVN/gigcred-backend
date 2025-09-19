package com.gigcred.payments.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Transfer {
    private final String id;
    private final String quoteId;
    private final String userId;
    private final String sourceAccountId;
    private final String beneficiaryId;
    private final BigDecimal sourceAmount;
    private final BigDecimal destinationAmount;
    private String status;
    private final Instant createdAt;
    private Instant updatedAt;
    private final String providerReference;

    private Transfer(
        String id,
        String quoteId,
        String userId,
        String sourceAccountId,
        String beneficiaryId,
        BigDecimal sourceAmount,
        BigDecimal destinationAmount,
        String status,
        Instant createdAt,
        Instant updatedAt,
        String providerReference
    ) {
        this.id = id;
        this.quoteId = quoteId;
        this.userId = userId;
        this.sourceAccountId = sourceAccountId;
        this.beneficiaryId = beneficiaryId;
        this.sourceAmount = sourceAmount;
        this.destinationAmount = destinationAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.providerReference = providerReference;
    }

    public static Transfer create(String quoteId, String userId, String sourceAccountId, String beneficiaryId, BigDecimal sourceAmount, BigDecimal destinationAmount, String providerReference) {
        Instant now = Instant.now();
        return new Transfer(UUID.randomUUID().toString(), quoteId, userId, sourceAccountId, beneficiaryId, sourceAmount, destinationAmount, "PENDING", now, now, providerReference);
    }

    public void markStatus(String status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public String id() {
        return id;
    }

    public String quoteId() {
        return quoteId;
    }

    public String userId() {
        return userId;
    }

    public String sourceAccountId() {
        return sourceAccountId;
    }

    public String beneficiaryId() {
        return beneficiaryId;
    }

    public BigDecimal sourceAmount() {
        return sourceAmount;
    }

    public BigDecimal destinationAmount() {
        return destinationAmount;
    }

    public String status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public String providerReference() {
        return providerReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transfer transfer)) return false;
        return id.equals(transfer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
