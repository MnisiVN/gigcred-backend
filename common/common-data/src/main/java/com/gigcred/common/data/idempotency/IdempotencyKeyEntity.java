package com.gigcred.common.data.idempotency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKeyEntity {

    @Id
    @Column(name = "key", nullable = false, length = 128)
    private String key;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "fingerprint", nullable = false, length = 256)
    private String fingerprint;

    @Column(name = "response_hash")
    private String responseHash;

    protected IdempotencyKeyEntity() {
    }

    public IdempotencyKeyEntity(String key, Instant createdAt, String fingerprint, String responseHash) {
        this.key = key;
        this.createdAt = createdAt;
        this.fingerprint = fingerprint;
        this.responseHash = responseHash;
    }

    public String getKey() {
        return key;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getResponseHash() {
        return responseHash;
    }
}
