package com.gigcred.common.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Value object for idempotency keys passed on money-moving requests.
 */
public final class IdempotencyKey {
    private final String value;
    private final Instant createdAt;

    public IdempotencyKey(String value) {
        this(value, Instant.now());
    }

    public IdempotencyKey(String value, Instant createdAt) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Idempotency key must be provided");
        }
        this.value = value;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public String value() {
        return value;
    }

    public Instant createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdempotencyKey that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
