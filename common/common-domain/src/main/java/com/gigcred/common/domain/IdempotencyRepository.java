package com.gigcred.common.domain;

import java.util.Optional;

public interface IdempotencyRepository {
    Optional<IdempotencyRecord> find(String key);

    IdempotencyRecord save(IdempotencyRecord record);
}
