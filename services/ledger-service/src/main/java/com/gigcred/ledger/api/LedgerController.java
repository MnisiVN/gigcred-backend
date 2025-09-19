package com.gigcred.ledger.api;

import com.gigcred.ledger.application.LedgerService;
import com.gigcred.ledger.application.LedgerService.LedgerReconciliationResult;
import com.gigcred.ledger.domain.LedgerTransaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ledger")
@Tag(name = "Ledger", description = "Ledger operations")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Reserve funds for a transaction")
    public ResponseEntity<LedgerTransaction> reserve(@Valid @RequestBody ReserveRequest request) {
        LedgerTransaction transaction = ledgerService.reserve(request.reference(), request.debitAccountId(), request.creditAccountId(), request.amountCents());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(transaction);
    }

    @PostMapping("/transactions/{transactionId}/post")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Post a pending transaction")
    public ResponseEntity<LedgerTransaction> post(@PathVariable String transactionId) {
        return ResponseEntity.ok(ledgerService.post(transactionId));
    }

    @GetMapping("/transactions/pending")
    @PreAuthorize("hasAuthority('SCOPE_wallet:read')")
    @Operation(summary = "List pending transactions")
    public ResponseEntity<List<LedgerTransaction>> listPending() {
        return ResponseEntity.ok(ledgerService.listPending());
    }

    @PostMapping("/reconcile")
    @PreAuthorize("hasAuthority('SCOPE_wallet:read')")
    @Operation(summary = "Run reconciliation against provider balances")
    public ResponseEntity<LedgerReconciliationResult> reconcile(@RequestBody Map<String, Long> providerBalances) {
        return ResponseEntity.ok(ledgerService.reconcile(providerBalances));
    }

    public record ReserveRequest(String reference, String debitAccountId, String creditAccountId, long amountCents) {}
}
