package com.gigcred.ops.domain;

import java.time.Instant;

public record OutboxMessage(String id, String aggregate, String aggregateId, String type, String payload, Instant createdAt, int tries) {
}
