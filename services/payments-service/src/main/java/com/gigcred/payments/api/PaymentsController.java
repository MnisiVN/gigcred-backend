package com.gigcred.payments.api;

import com.gigcred.common.domain.IdempotencyKey;
import com.gigcred.payments.application.QuoteService;
import com.gigcred.payments.application.TransferService;
import com.gigcred.payments.application.TransferService.TransferRequest;
import com.gigcred.payments.domain.Quote;
import com.gigcred.payments.domain.Transfer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payments and remittances")
public class PaymentsController {

    private final QuoteService quoteService;
    private final TransferService transferService;

    public PaymentsController(QuoteService quoteService, TransferService transferService) {
        this.quoteService = quoteService;
        this.transferService = transferService;
    }

    @PostMapping("/quotes")
    @PreAuthorize("hasAuthority('SCOPE_remit:write')")
    @Operation(summary = "Create a FX quote")
    public ResponseEntity<Quote> createQuote(@Valid @RequestBody QuoteRequest request) {
        Quote quote = quoteService.createQuote(
            request.userId(),
            request.sourceCurrency(),
            request.destinationCurrency(),
            request.sourceAmount(),
            request.providerRate(),
            request.markupPct(),
            request.platformFeePct()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(quote);
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasAuthority('SCOPE_remit:write')")
    @Operation(summary = "Create a cross-border transfer")
    public ResponseEntity<Transfer> createTransfer(
        @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey,
        @Valid @RequestBody TransferRequestBody request
    ) {
        Transfer transfer = transferService.createTransfer(
            new IdempotencyKey(idempotencyKey),
            new TransferRequest(
                request.quoteId(),
                request.sourceAccountId(),
                request.beneficiaryId(),
                request.destinationCountry(),
                request.clientReference()
            )
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(transfer);
    }

    @PostMapping("/webhooks/provider")
    @Operation(summary = "Handle payout webhooks")
    public ResponseEntity<Void> providerWebhook(@RequestParam String reference, @RequestParam String status) {
        transferService.handleWebhook(reference, status);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/transfers/retry")
    @PreAuthorize("hasAuthority('SCOPE_remit:write')")
    @Operation(summary = "Retry pending transfers")
    public ResponseEntity<List<Transfer>> retry() {
        return ResponseEntity.ok(transferService.retryPending());
    }

    @GetMapping("/transfers")
    @PreAuthorize("hasAuthority('SCOPE_remit:read')")
    @Operation(summary = "List transfers")
    public ResponseEntity<List<Transfer>> listTransfers() {
        return ResponseEntity.ok(transferService.listTransfers());
    }

    public record QuoteRequest(
        String userId,
        String sourceCurrency,
        String destinationCurrency,
        BigDecimal sourceAmount,
        BigDecimal providerRate,
        BigDecimal markupPct,
        BigDecimal platformFeePct
    ) {}

    public record TransferRequestBody(
        String quoteId,
        String sourceAccountId,
        String beneficiaryId,
        String destinationCountry,
        String clientReference
    ) {}
}
