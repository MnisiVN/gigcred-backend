package com.gigcred.ledger.application;

import com.gigcred.ledger.domain.LedgerEntry;
import com.gigcred.ledger.domain.LedgerEntry.EntryType;
import com.gigcred.ledger.domain.LedgerTransaction;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class LedgerService {

    private final Map<String, LedgerTransaction> transactions = new ConcurrentHashMap<>();
    private final Map<String, Long> postedBalances = new ConcurrentHashMap<>();

    public LedgerTransaction reserve(String reference, String debitAccountId, String creditAccountId, long amountCents) {
        LedgerTransaction transaction = LedgerTransaction.pending(reference);
        transaction.addEntry(new LedgerEntry(debitAccountId, -amountCents, EntryType.DEBIT, Instant.now()));
        transaction.addEntry(new LedgerEntry(creditAccountId, amountCents, EntryType.CREDIT, Instant.now()));
        transactions.put(transaction.id(), transaction);
        return transaction;
    }

    public Optional<LedgerTransaction> find(String id) {
        return Optional.ofNullable(transactions.get(id));
    }

    public LedgerTransaction post(String transactionId) {
        LedgerTransaction transaction = transactions.get(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction not found");
        }
        transaction.post();
        transaction.entries().forEach(entry -> postedBalances.merge(entry.accountId(), entry.amountCents(), Long::sum));
        return transaction;
    }

    public List<LedgerTransaction> listPending() {
        return transactions.values().stream().filter(tx -> "PENDING".equals(tx.status())).toList();
    }

    public LedgerReconciliationResult reconcile(Map<String, Long> providerBalances) {
        List<LedgerReconciliationResult.Mismatch> mismatches = new ArrayList<>();
        postedBalances.forEach((accountId, balance) -> {
            long providerBalance = providerBalances.getOrDefault(accountId, 0L);
            if (providerBalance != balance) {
                mismatches.add(new LedgerReconciliationResult.Mismatch(accountId, balance, providerBalance));
            }
        });
        providerBalances.forEach((accountId, balance) -> {
            if (!postedBalances.containsKey(accountId)) {
                mismatches.add(new LedgerReconciliationResult.Mismatch(accountId, 0L, balance));
            }
        });
        return new LedgerReconciliationResult(mismatches);
    }

    public record LedgerReconciliationResult(List<Mismatch> mismatches) {
        public boolean inBalance() {
            return mismatches.isEmpty();
        }

        public record Mismatch(String accountId, long internalBalance, long providerBalance) {}
    }
}
