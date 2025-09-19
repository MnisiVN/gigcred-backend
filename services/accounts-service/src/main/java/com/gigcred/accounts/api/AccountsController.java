package com.gigcred.accounts.api;

import com.gigcred.accounts.application.AccountService;
import com.gigcred.accounts.domain.AccountSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
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
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Accounts and wallet operations")
public class AccountsController {

    private final AccountService accountService;

    public AccountsController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('SCOPE_wallet:read')")
    @Operation(summary = "Retrieve the wallet account summary for a user")
    public ResponseEntity<AccountSummary> getSummary(@PathVariable String userId) {
        return accountService.find(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{userId}/sync")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Upsert wallet summary (used by event processors)")
    public ResponseEntity<AccountSummary> sync(@PathVariable String userId, @RequestBody List<AccountSummary.WalletBalance> balances) {
        return ResponseEntity.ok(accountService.upsert(new AccountSummary(userId, balances, Instant.now())));
    }
}
