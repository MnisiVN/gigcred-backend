package com.gigcred.common.domain;

import java.time.Instant;
import java.util.Objects;

public final class OutboxEvent {
    private final String aggregate;
    private final String aggregateId;
    private final String type;
    private final String payloadJson;
    private final Instant createdAt;

    public OutboxEvent(String aggregate, String aggregateId, String type, String payloadJson, Instant createdAt) {
        this.aggregate = Objects.requireNonNull(aggregate);
        this.aggregateId = Objects.requireNonNull(aggregateId);
        this.type = Objects.requireNonNull(type);
        this.payloadJson = Objects.requireNonNull(payloadJson);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    public String aggregate() {
        return aggregate;
    }

    public String aggregateId() {
        return aggregateId;
    }

    public String type() {
        return type;
    }

    public String payloadJson() {
        return payloadJson;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
