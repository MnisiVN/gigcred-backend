package com.gigcred.onboarding.api;

import com.gigcred.integrations.flutterwave.dto.KycRequest;
import com.gigcred.onboarding.application.OnboardingWorkflowService;
import com.gigcred.onboarding.domain.OnboardingRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/onboarding")
@Tag(name = "Onboarding", description = "Onboarding and KYC management")
public class OnboardingController {

    private final OnboardingWorkflowService workflowService;

    public OnboardingController(OnboardingWorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/{userId}/start")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Kick off KYC flow for a user")
    public ResponseEntity<OnboardingRecord> start(@PathVariable String userId, @Valid @RequestBody KycRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(workflowService.start(userId, request));
    }

    @PostMapping("/{userId}/documents")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Attach a document reference to the onboarding record")
    public ResponseEntity<OnboardingRecord> uploadDocument(
        @PathVariable String userId,
        @RequestParam @NotBlank String documentReference
    ) {
        return ResponseEntity.accepted().body(workflowService.uploadDocument(userId, documentReference));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_wallet:read')")
    @Operation(summary = "Fetch onboarding status")
    public ResponseEntity<OnboardingRecord> status(@PathVariable String userId) {
        return workflowService.find(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/webhooks/kyc")
    @Operation(summary = "Handle Flutterwave KYC webhook callbacks")
    public ResponseEntity<Void> webhook(@RequestParam String userId, @RequestParam String status) {
        workflowService.handleWebhook(userId, status);
        return ResponseEntity.accepted().build();
    }
}
