package com.gigcred.common.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public final class AuditLogEntry {
    private final String actor;
    private final String action;
    private final String resource;
    private final String correlationId;
    private final Instant createdAt;
    private final Map<String, Object> details;

    public AuditLogEntry(String actor, String action, String resource, String correlationId, Instant createdAt, Map<String, Object> details) {
        this.actor = Objects.requireNonNull(actor);
        this.action = Objects.requireNonNull(action);
        this.resource = Objects.requireNonNull(resource);
        this.correlationId = Objects.requireNonNull(correlationId);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.details = Map.copyOf(details);
    }

    public String actor() {
        return actor;
    }

    public String action() {
        return action;
    }

    public String resource() {
        return resource;
    }

    public String correlationId() {
        return correlationId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Map<String, Object> details() {
        return details;
    }
}
