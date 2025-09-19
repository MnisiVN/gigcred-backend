package com.gigcred.gateway.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Gateway", description = "Gateway and profile endpoints")
public class GatewayController {

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('SCOPE_wallet:read')")
    @Operation(summary = "Fetch the authenticated user's profile summary", responses = {
        @ApiResponse(responseCode = "200", description = "Profile summary", content = @Content(schema = @Schema(implementation = ProfileResponse.class)))
    })
    public ResponseEntity<ProfileResponse> profile() {
        ProfileResponse response = new ProfileResponse(
            "user-123",
            "APPROVED",
            List.of(new FeatureFlag("remittances", true), new FeatureFlag("credit-builder", true))
        );
        return ResponseEntity.ok(response);
    }

    public record ProfileResponse(String userId, String kycStatus, List<FeatureFlag> features) {}

    public record FeatureFlag(String name, boolean enabled) {}

    @GetMapping("/limits")
    @PreAuthorize("hasAuthority('SCOPE_wallet:read')")
    @Operation(summary = "Retrieve spend limits for the authenticated user")
    public ResponseEntity<Map<String, Object>> limits() {
        return ResponseEntity.ok(Map.of(
            "daily_send_limit", 100_000,
            "daily_withdraw_limit", 50_000,
            "currency", "ZAR"
        ));
    }
}
