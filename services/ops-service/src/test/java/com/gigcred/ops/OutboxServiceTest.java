package com.gigcred.ops;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.ops.application.OutboxService;
import org.junit.jupiter.api.Test;

class OutboxServiceTest {

    private final OutboxService service = new OutboxService();

    @Test
    void enqueueAndAck() {
        var message = service.enqueue("transfer", "123", "TRANSFER_CREATED", "{}");
        assertThat(service.fetchPending()).hasSize(1);
        service.markProcessed(message.id());
        assertThat(service.fetchPending()).isEmpty();
    }
}
