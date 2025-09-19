package com.gigcred.payments;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gigcred.common.domain.IdempotencyKey;
import com.gigcred.common.domain.InMemoryIdempotencyService;
import com.gigcred.integrations.flutterwave.FlutterwaveClientFacade;
import com.gigcred.integrations.flutterwave.dto.PayoutRequest;
import com.gigcred.integrations.flutterwave.dto.PayoutResponse;
import com.gigcred.payments.application.QuoteService;
import com.gigcred.payments.application.TransferService;
import com.gigcred.payments.application.TransferService.TransferRequest;
import com.gigcred.payments.domain.Quote;
import com.gigcred.payments.domain.Transfer;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private FlutterwaveClientFacade flutterwaveClientFacade;

    private TransferService transferService;
    private Quote quote;

    @BeforeEach
    void setUp() {
        QuoteService quoteService = new QuoteService();
        quote = quoteService.createQuote("user-1", "ZAR", "USD", new BigDecimal("100.00"), new BigDecimal("0.052"), new BigDecimal("0.02"), new BigDecimal("0.01"));
        transferService = new TransferService(quoteService, flutterwaveClientFacade, new InMemoryIdempotencyService());
    }

    @Test
    void idempotencyPreventsDoubleSpend() {
        when(flutterwaveClientFacade.initiatePayout(eq("idem-1"), any(PayoutRequest.class)))
            .thenReturn(new PayoutResponse("PENDING", "prov-1", Instant.now(), "accepted"));

        Transfer first = transferService.createTransfer(new IdempotencyKey("idem-1"), buildRequest());
        Transfer second = transferService.createTransfer(new IdempotencyKey("idem-1"), buildRequest());

        assertThat(first.id()).isEqualTo(second.id());
        verify(flutterwaveClientFacade, times(1)).initiatePayout(eq("idem-1"), any(PayoutRequest.class));
    }

    @Test
    void webhookArrivingBeforeApiResponseResolvesState() {
        transferService.handleWebhook("prov-2", "SUCCESS");
        when(flutterwaveClientFacade.initiatePayout(eq("idem-2"), any(PayoutRequest.class)))
            .thenReturn(new PayoutResponse("PENDING", "prov-2", Instant.now(), "accepted"));

        Transfer transfer = transferService.createTransfer(new IdempotencyKey("idem-2"), buildRequest());

        assertThat(transfer.status()).isEqualTo("SUCCESS");
    }

    @Test
    void retryFlowOnlyPostsOnce() {
        when(flutterwaveClientFacade.initiatePayout(any(), any(PayoutRequest.class)))
            .thenReturn(new PayoutResponse("PENDING", "prov-3", Instant.now(), "accepted"));

        Transfer transfer = transferService.createTransfer(new IdempotencyKey("idem-3"), buildRequest());
        List<Transfer> retried = transferService.retryPending();

        assertThat(retried).contains(transfer);

        transferService.handleWebhook("prov-3", "POSTED");
        assertThat(transferService.findById(transfer.id())).isPresent().get().extracting(Transfer::status).isEqualTo("POSTED");

        ArgumentCaptor<PayoutRequest> requestCaptor = ArgumentCaptor.forClass(PayoutRequest.class);
        verify(flutterwaveClientFacade, times(2)).initiatePayout(any(), requestCaptor.capture());
        assertThat(requestCaptor.getAllValues()).hasSize(2);
    }

    private TransferRequest buildRequest() {
        return new TransferRequest(
            quote.id(),
            "acct-1",
            "benef-1",
            "NG",
            "client-ref"
        );
    }
}
