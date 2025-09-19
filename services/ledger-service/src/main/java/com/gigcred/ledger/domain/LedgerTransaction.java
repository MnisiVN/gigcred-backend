package com.gigcred.ledger.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LedgerTransaction {
    private final String id;
    private final String reference;
    private final List<LedgerEntry> entries = new ArrayList<>();
    private String status;
    private final Instant createdAt;
    private Instant postedAt;

    private LedgerTransaction(String id, String reference, String status, Instant createdAt) {
        this.id = id;
        this.reference = reference;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static LedgerTransaction pending(String reference) {
        return new LedgerTransaction(UUID.randomUUID().toString(), reference, "PENDING", Instant.now());
    }

    public void addEntry(LedgerEntry entry) {
        entries.add(entry);
    }

    public void post() {
        this.status = "POSTED";
        this.postedAt = Instant.now();
    }

    public String id() {
        return id;
    }

    public String reference() {
        return reference;
    }

    public String status() {
        return status;
    }

    public List<LedgerEntry> entries() {
        return Collections.unmodifiableList(entries);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant postedAt() {
        return postedAt;
    }
}
