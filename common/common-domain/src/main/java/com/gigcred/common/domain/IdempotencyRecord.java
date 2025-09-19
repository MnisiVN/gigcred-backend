package com.gigcred.common.domain;

import java.time.Instant;
import java.util.Objects;

public final class IdempotencyRecord {
    private final IdempotencyKey key;
    private final String fingerprint;
    private final Instant createdAt;
    private final String responseHash;

    public IdempotencyRecord(IdempotencyKey key, String fingerprint, Instant createdAt, String responseHash) {
        this.key = Objects.requireNonNull(key, "key");
        this.fingerprint = Objects.requireNonNull(fingerprint, "fingerprint");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.responseHash = responseHash;
    }

    public IdempotencyKey key() {
        return key;
    }

    public String fingerprint() {
        return fingerprint;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public String responseHash() {
        return responseHash;
    }
}
