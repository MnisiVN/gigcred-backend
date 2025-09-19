package com.gigcred.ledger.domain;

import java.time.Instant;

public record LedgerEntry(
    String accountId,
    long amountCents,
    EntryType type,
    Instant createdAt
) {
    public enum EntryType {
        DEBIT, CREDIT
    }
}
