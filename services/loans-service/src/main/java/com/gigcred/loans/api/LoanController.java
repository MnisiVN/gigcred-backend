package com.gigcred.loans.api;

import com.gigcred.loans.application.LoanService;
import com.gigcred.loans.domain.LoanAgreement;
import com.gigcred.loans.domain.LoanOffer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans")
@Tag(name = "Loans", description = "Loan offers and agreements")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/offers")
    @PreAuthorize("hasAuthority('SCOPE_loans:write')")
    @Operation(summary = "Generate loan offers for a user")
    public ResponseEntity<List<LoanOffer>> generate(@RequestBody OfferRequest request) {
        return ResponseEntity.ok(loanService.generateOffers(request.userId(), request.score()));
    }

    @PostMapping("/offers/{offerId}/accept")
    @PreAuthorize("hasAuthority('SCOPE_loans:write')")
    @Operation(summary = "Accept an offer")
    public ResponseEntity<LoanAgreement> accept(@PathVariable String offerId) {
        return loanService.acceptOffer(offerId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/agreements/{agreementId}")
    @PreAuthorize("hasAuthority('SCOPE_loans:read')")
    @Operation(summary = "Get a loan agreement")
    public ResponseEntity<LoanAgreement> getAgreement(@PathVariable String agreementId) {
        return loanService.findAgreement(agreementId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    public record OfferRequest(@NotBlank String userId, @Min(300) int score) {}
}
