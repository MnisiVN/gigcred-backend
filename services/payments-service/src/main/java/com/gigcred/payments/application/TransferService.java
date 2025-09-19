package com.gigcred.payments.application;

import com.gigcred.common.domain.IdempotencyKey;
import com.gigcred.common.domain.IdempotencyService;
import com.gigcred.integrations.flutterwave.FlutterwaveClientFacade;
import com.gigcred.integrations.flutterwave.dto.PayoutRequest;
import com.gigcred.integrations.flutterwave.dto.PayoutResponse;
import com.gigcred.payments.domain.Quote;
import com.gigcred.payments.domain.Transfer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final QuoteService quoteService;
    private final FlutterwaveClientFacade flutterwaveClientFacade;
    private final IdempotencyService idempotencyService;
    private final Map<String, Transfer> transfers = new ConcurrentHashMap<>();
    private final Map<String, String> providerIndex = new ConcurrentHashMap<>();
    private final Map<String, String> webhookStatusBuffer = new ConcurrentHashMap<>();

    public TransferService(QuoteService quoteService, FlutterwaveClientFacade flutterwaveClientFacade, IdempotencyService idempotencyService) {
        this.quoteService = quoteService;
        this.flutterwaveClientFacade = flutterwaveClientFacade;
        this.idempotencyService = idempotencyService;
    }

    public Transfer createTransfer(IdempotencyKey idempotencyKey, TransferRequest request) {
        return idempotencyService.execute(idempotencyKey, () -> {
            Quote quote = quoteService.findById(request.quoteId())
                .orElseThrow(() -> new IllegalArgumentException("Quote not found"));

            PayoutResponse response = flutterwaveClientFacade.initiatePayout(
                idempotencyKey.value(),
                new PayoutRequest(
                    quote.sourceCurrency(),
                    quote.destinationCurrency(),
                    quote.sourceAmount().movePointRight(2).longValue(),
                    request.beneficiaryId(),
                    request.destinationCountry(),
                    request.clientReference()
                )
            );

            Transfer transfer = Transfer.create(
                quote.id(),
                quote.userId(),
                request.sourceAccountId(),
                request.beneficiaryId(),
                quote.sourceAmount(),
                quote.sourceAmount().multiply(quote.providerRate()),
                response.reference()
            );
            transfers.put(transfer.id(), transfer);
            providerIndex.put(response.reference(), transfer.id());

            Optional.ofNullable(webhookStatusBuffer.remove(response.reference()))
                .ifPresent(transfer::markStatus);

            return transfer;
        });
    }

    public void handleWebhook(String providerReference, String status) {
        String transferId = providerIndex.get(providerReference);
        if (transferId == null) {
            log.info("Buffering webhook for reference {} with status {}", providerReference, status);
            webhookStatusBuffer.put(providerReference, status);
            return;
        }
        transfers.computeIfPresent(transferId, (id, transfer) -> {
            transfer.markStatus(status);
            return transfer;
        });
    }

    public Optional<Transfer> findById(String id) {
        return Optional.ofNullable(transfers.get(id));
    }

    public List<Transfer> listTransfers() {
        return new ArrayList<>(transfers.values());
    }

    public List<Transfer> retryPending() {
        List<Transfer> retried = new ArrayList<>();
        transfers.values().stream()
            .filter(transfer -> "PENDING".equals(transfer.status()))
            .forEach(transfer -> {
                try {
                    PayoutResponse response = flutterwaveClientFacade.initiatePayout(
                        transfer.providerReference(),
                        new PayoutRequest(
                            "ZAR",
                            "ZAR",
                            transfer.sourceAmount().movePointRight(2).longValue(),
                            transfer.beneficiaryId(),
                            "ZA",
                            transfer.id()
                        )
                    );
                    providerIndex.put(response.reference(), transfer.id());
                    retried.add(transfer);
                } catch (Exception ex) {
                    log.warn("Retry failed for transfer {}", transfer.id(), ex);
                }
            });
        return retried;
    }

    public record TransferRequest(
        String quoteId,
        String sourceAccountId,
        String beneficiaryId,
        String destinationCountry,
        String clientReference
    ) {
    }
}
