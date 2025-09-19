package com.gigcred.common.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class InMemoryIdempotencyServiceTest {

    @Test
    void executesActionOnlyOnceForSameKey() {
        InMemoryIdempotencyService service = new InMemoryIdempotencyService();
        AtomicInteger counter = new AtomicInteger();

        String first = service.execute(new IdempotencyKey("abc"), () -> {
            counter.incrementAndGet();
            return "response";
        });

        String second = service.execute(new IdempotencyKey("abc"), () -> {
            counter.incrementAndGet();
            return "other";
        });

        assertThat(first).isEqualTo("response");
        assertThat(second).isEqualTo("response");
        assertThat(counter.get()).isEqualTo(1);
    }
}
