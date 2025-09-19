package com.gigcred.scoring.application;

import com.gigcred.scoring.domain.ScoreSnapshot;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ScoreService {

    private final Map<String, ScoreSnapshot> snapshots = new ConcurrentHashMap<>();

    public ScoreSnapshot compute(String userId, int onTimePayments, int missedPayments, long totalVolumeCents) {
        int baseScore = 500 + onTimePayments * 10 - missedPayments * 40;
        if (totalVolumeCents > 500_000) {
            baseScore += 40;
        }
        int capped = Math.max(300, Math.min(900, baseScore));
        String tier = capped >= 750 ? "PLATINUM" : capped >= 650 ? "GOLD" : "STANDARD";
        ScoreSnapshot snapshot = new ScoreSnapshot(userId, capped, Map.of(
            "on_time_payments", onTimePayments,
            "missed_payments", missedPayments,
            "total_volume_cents", totalVolumeCents
        ), tier, Instant.now());
        snapshots.put(userId, snapshot);
        return snapshot;
    }

    public Optional<ScoreSnapshot> find(String userId) {
        return Optional.ofNullable(snapshots.get(userId));
    }
}
