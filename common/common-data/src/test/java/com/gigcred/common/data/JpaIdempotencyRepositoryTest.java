package com.gigcred.common.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.common.data.idempotency.JpaIdempotencyRepository;
import com.gigcred.common.domain.IdempotencyKey;
import com.gigcred.common.domain.IdempotencyRecord;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class JpaIdempotencyRepositoryTest {

    @Autowired
    private JpaIdempotencyRepository repository;

    @Test
    void savesAndLoadsRecord() {
        IdempotencyRecord record = new IdempotencyRecord(new IdempotencyKey("abc", Instant.parse("2024-01-01T00:00:00Z")), "fingerprint", Instant.parse("2024-01-01T00:00:00Z"), "hash");
        repository.save(record);

        assertThat(repository.find("abc")).isPresent();
    }
}
