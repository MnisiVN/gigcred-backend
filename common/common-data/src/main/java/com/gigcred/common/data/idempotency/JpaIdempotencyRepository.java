package com.gigcred.common.data.idempotency;

import com.gigcred.common.domain.IdempotencyKey;
import com.gigcred.common.domain.IdempotencyRecord;
import com.gigcred.common.domain.IdempotencyRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaIdempotencyRepository implements IdempotencyRepository {

    private final IdempotencyKeyJpaRepository repository;

    public JpaIdempotencyRepository(IdempotencyKeyJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<IdempotencyRecord> find(String key) {
        return repository.findById(key).map(entity ->
            new IdempotencyRecord(
                new IdempotencyKey(entity.getKey(), entity.getCreatedAt()),
                entity.getFingerprint(),
                entity.getCreatedAt(),
                entity.getResponseHash()
            )
        );
    }

    @Override
    public IdempotencyRecord save(IdempotencyRecord record) {
        repository.save(new IdempotencyKeyEntity(
            record.key().value(),
            record.createdAt(),
            record.fingerprint(),
            record.responseHash()
        ));
        return record;
    }
}
