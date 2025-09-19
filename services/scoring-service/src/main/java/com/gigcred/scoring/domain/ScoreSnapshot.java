package com.gigcred.scoring.domain;

import java.time.Instant;
import java.util.Map;

public record ScoreSnapshot(String userId, int score, Map<String, Object> features, String tier, Instant updatedAt) {
}
