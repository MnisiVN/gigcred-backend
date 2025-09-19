package com.gigcred.common.domain;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Simple in-memory implementation used for local testing and unit tests.
 */
public final class InMemoryIdempotencyService implements IdempotencyService, IdempotencyRepository {

    private final Map<String, IdempotencyRecord> store = new ConcurrentHashMap<>();
    private final Map<String, Object> responses = new ConcurrentHashMap<>();

    @Override
    public Optional<IdempotencyRecord> find(String key) {
        return Optional.ofNullable(store.get(key));
    }

    @Override
    public IdempotencyRecord save(IdempotencyRecord record) {
        store.put(record.key().value(), record);
        return record;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T execute(IdempotencyKey key, Supplier<T> action) {
        IdempotencyRecord existing = store.get(key.value());
        if (existing != null && responses.containsKey(key.value())) {
            return (T) responses.get(key.value());
        }
        T result = action.get();
        store.put(key.value(), new IdempotencyRecord(key, key.value(), Instant.now(), null));
        responses.put(key.value(), result);
        return result;
    }
}
