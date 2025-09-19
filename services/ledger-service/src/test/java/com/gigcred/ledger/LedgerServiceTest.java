package com.gigcred.ledger;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.ledger.application.LedgerService;
import com.gigcred.ledger.application.LedgerService.LedgerReconciliationResult;
import com.gigcred.ledger.domain.LedgerTransaction;
import java.util.Map;
import org.junit.jupiter.api.Test;

class LedgerServiceTest {

    private final LedgerService ledgerService = new LedgerService();

    @Test
    void reconciliationDetectsMismatches() {
        LedgerTransaction tx = ledgerService.reserve("ref-1", "acct-1", "acct-2", 10_00);
        ledgerService.post(tx.id());

        LedgerReconciliationResult result = ledgerService.reconcile(Map.of(
            "acct-1", -9_00,
            "acct-2", 10_00
        ));

        assertThat(result.inBalance()).isFalse();
        assertThat(result.mismatches()).anyMatch(m -> m.accountId().equals("acct-1"));
    }
}
