package com.gigcred.compliance.api;

import com.gigcred.compliance.application.ComplianceService;
import com.gigcred.compliance.domain.ComplianceCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compliance")
@Tag(name = "Compliance", description = "Compliance screening")
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @PostMapping("/screenings")
    @PreAuthorize("hasAuthority('SCOPE_compliance:write')")
    @Operation(summary = "Execute a compliance screening")
    public ResponseEntity<ComplianceCase> screen(@RequestBody ScreeningRequest request) {
        return ResponseEntity.ok(complianceService.runScreening(request.userId(), request.pepMatch(), request.velocityAlerts()));
    }

    @GetMapping("/cases/{caseId}")
    @PreAuthorize("hasAuthority('SCOPE_compliance:read')")
    @Operation(summary = "Get compliance case")
    public ResponseEntity<ComplianceCase> get(@PathVariable String caseId) {
        return complianceService.find(caseId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    public record ScreeningRequest(String userId, boolean pepMatch, int velocityAlerts) {}
}
