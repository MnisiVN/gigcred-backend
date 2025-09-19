package com.gigcred.payments.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public class Quote {
    private final String id;
    private final String userId;
    private final String sourceCurrency;
    private final String destinationCurrency;
    private final BigDecimal sourceAmount;
    private final BigDecimal providerRate;
    private final BigDecimal markup;
    private final BigDecimal platformFee;
    private final Instant expiresAt;

    private Quote(
        String id,
        String userId,
        String sourceCurrency,
        String destinationCurrency,
        BigDecimal sourceAmount,
        BigDecimal providerRate,
        BigDecimal markup,
        BigDecimal platformFee,
        Instant expiresAt
    ) {
        this.id = id;
        this.userId = userId;
        this.sourceCurrency = sourceCurrency;
        this.destinationCurrency = destinationCurrency;
        this.sourceAmount = sourceAmount;
        this.providerRate = providerRate;
        this.markup = markup;
        this.platformFee = platformFee;
        this.expiresAt = expiresAt;
    }

    public static Quote create(String userId, String sourceCurrency, String destinationCurrency, BigDecimal sourceAmount, BigDecimal providerRate, BigDecimal markupPct, BigDecimal platformFeePct) {
        BigDecimal markup = sourceAmount.multiply(providerRate).multiply(markupPct).setScale(2, RoundingMode.HALF_UP);
        BigDecimal platformFee = sourceAmount.multiply(platformFeePct).setScale(2, RoundingMode.HALF_UP);
        return new Quote(
            UUID.randomUUID().toString(),
            userId,
            sourceCurrency,
            destinationCurrency,
            sourceAmount.setScale(2, RoundingMode.HALF_UP),
            providerRate,
            markup,
            platformFee,
            Instant.now().plusSeconds(300)
        );
    }

    public String id() {
        return id;
    }

    public String userId() {
        return userId;
    }

    public String sourceCurrency() {
        return sourceCurrency;
    }

    public String destinationCurrency() {
        return destinationCurrency;
    }

    public BigDecimal sourceAmount() {
        return sourceAmount;
    }

    public BigDecimal providerRate() {
        return providerRate;
    }

    public BigDecimal markup() {
        return markup;
    }

    public BigDecimal platformFee() {
        return platformFee;
    }

    public Instant expiresAt() {
        return expiresAt;
    }
}
