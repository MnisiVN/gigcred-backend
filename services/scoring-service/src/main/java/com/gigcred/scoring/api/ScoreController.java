package com.gigcred.scoring.api;

import com.gigcred.scoring.application.ScoreService;
import com.gigcred.scoring.domain.ScoreSnapshot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scoring")
@Tag(name = "Scoring", description = "GigCred score endpoints")
public class ScoreController {

    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/compute")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Compute a score for a user")
    public ResponseEntity<ScoreSnapshot> compute(@RequestBody ComputeRequest request) {
        return ResponseEntity.ok(scoreService.compute(request.userId(), request.onTimePayments(), request.missedPayments(), request.totalVolumeCents()));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_wallet:read')")
    @Operation(summary = "Get the latest score snapshot")
    public ResponseEntity<ScoreSnapshot> get(@PathVariable String userId) {
        return scoreService.find(userId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    public record ComputeRequest(@NotBlank String userId, @Min(0) int onTimePayments, @Min(0) int missedPayments, long totalVolumeCents) {}
}
