package com.gigcred.common.domain;

import java.util.function.Supplier;

public interface IdempotencyService {
    <T> T execute(IdempotencyKey key, Supplier<T> action);
}
