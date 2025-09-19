package com.gigcred.ops.api;

import com.gigcred.ops.application.OutboxService;
import com.gigcred.ops.domain.OutboxMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ops")
@Tag(name = "Ops", description = "Operational tooling")
public class OpsController {

    private final OutboxService outboxService;

    public OpsController(OutboxService outboxService) {
        this.outboxService = outboxService;
    }

    @PostMapping("/outbox")
    @PreAuthorize("hasAuthority('SCOPE_ops:write')")
    @Operation(summary = "Enqueue an outbox event for testing")
    public ResponseEntity<OutboxMessage> enqueue(@RequestBody OutboxRequest request) {
        return ResponseEntity.ok(outboxService.enqueue(request.aggregate(), request.aggregateId(), request.type(), request.payload()));
    }

    @GetMapping("/outbox")
    @PreAuthorize("hasAuthority('SCOPE_ops:read')")
    @Operation(summary = "List pending outbox messages")
    public ResponseEntity<List<OutboxMessage>> list() {
        return ResponseEntity.ok(outboxService.fetchPending());
    }

    @PostMapping("/outbox/{messageId}/ack")
    @PreAuthorize("hasAuthority('SCOPE_ops:write')")
    @Operation(summary = "Acknowledge processing of an outbox message")
    public ResponseEntity<Void> acknowledge(@PathVariable String messageId) {
        outboxService.markProcessed(messageId);
        return ResponseEntity.accepted().build();
    }

    public record OutboxRequest(String aggregate, String aggregateId, String type, String payload) {}
}
