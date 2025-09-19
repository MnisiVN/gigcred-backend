package com.gigcred.common.domain;

import java.time.Instant;

public interface DomainEvent {
    String type();

    Instant occurredAt();
}
