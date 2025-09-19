package com.gigcred.scoring;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.scoring.application.ScoreService;
import org.junit.jupiter.api.Test;

class ScoreServiceTest {

    private final ScoreService scoreService = new ScoreService();

    @Test
    void computeAssignsTier() {
        var snapshot = scoreService.compute("user-1", 20, 0, 1_000_000);
        assertThat(snapshot.tier()).isEqualTo("PLATINUM");
        assertThat(scoreService.find("user-1")).isPresent();
    }
}
