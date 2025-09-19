package com.gigcred.ops.application;

import com.gigcred.ops.domain.OutboxMessage;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class OutboxService {

    private final Map<String, OutboxMessage> messages = new ConcurrentHashMap<>();

    public OutboxMessage enqueue(String aggregate, String aggregateId, String type, String payload) {
        OutboxMessage message = new OutboxMessage(UUID.randomUUID().toString(), aggregate, aggregateId, type, payload, Instant.now(), 0);
        messages.put(message.id(), message);
        return message;
    }

    public List<OutboxMessage> fetchPending() {
        return new ArrayList<>(messages.values());
    }

    public void markProcessed(String id) {
        messages.remove(id);
    }
}
