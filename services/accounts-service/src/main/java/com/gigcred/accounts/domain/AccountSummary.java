package com.gigcred.accounts.domain;

import java.time.Instant;
import java.util.List;

public record AccountSummary(String userId, List<WalletBalance> balances, Instant asOf) {
    public record WalletBalance(String accountId, String currency, long availableCents, long pendingCents) {}
}
