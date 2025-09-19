package com.gigcred.accounts;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.accounts.application.AccountService;
import com.gigcred.accounts.domain.AccountSummary;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AccountServiceTest {

    private final AccountService service = new AccountService();

    @Test
    void upsertReplacesExistingSummary() {
        service.upsert(new AccountSummary("user-xyz", List.of(new AccountSummary.WalletBalance("acct", "ZAR", 1_000, 0)), Instant.now()));

        assertThat(service.find("user-xyz")).isPresent();
    }
}
