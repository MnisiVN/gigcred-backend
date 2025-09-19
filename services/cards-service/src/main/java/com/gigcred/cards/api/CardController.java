package com.gigcred.cards.api;

import com.gigcred.cards.application.CardService;
import com.gigcred.cards.domain.Card;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Cards", description = "Card issuance and controls")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/{userId}/virtual")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Issue a virtual card")
    public ResponseEntity<Card> issueVirtual(@PathVariable String userId) {
        return ResponseEntity.ok(cardService.issueVirtualCard(userId));
    }

    @PostMapping("/{cardId}/freeze")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Freeze a card")
    public ResponseEntity<Card> freeze(@PathVariable String cardId) {
        return cardService.freeze(cardId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{cardId}/unfreeze")
    @PreAuthorize("hasAuthority('SCOPE_wallet:write')")
    @Operation(summary = "Unfreeze a card")
    public ResponseEntity<Card> unfreeze(@PathVariable String cardId) {
        return cardService.unfreeze(cardId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_wallet:read')")
    @Operation(summary = "List cards for a user")
    public ResponseEntity<List<Card>> list(@RequestParam @NotBlank String userId) {
        return ResponseEntity.ok(cardService.listForUser(userId));
    }
}
