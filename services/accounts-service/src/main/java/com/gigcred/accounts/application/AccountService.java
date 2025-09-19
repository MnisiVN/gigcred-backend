package com.gigcred.accounts.application;

import com.gigcred.accounts.domain.AccountSummary;
import com.gigcred.accounts.domain.AccountSummary.WalletBalance;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final Map<String, AccountSummary> summaries = new ConcurrentHashMap<>();

    public AccountService() {
        summaries.put("user-123", new AccountSummary("user-123", List.of(
            new WalletBalance("acct-main", "ZAR", 150_000L, 5_000L),
            new WalletBalance("acct-usd", "USD", 20_000L, 0L)
        ), Instant.now()));
    }

    public AccountSummary upsert(AccountSummary summary) {
        summaries.put(summary.userId(), summary);
        return summary;
    }

    public Optional<AccountSummary> find(String userId) {
        return Optional.ofNullable(summaries.get(userId));
    }
}
