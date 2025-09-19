package com.gigcred.cards;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.cards.application.CardService;
import com.gigcred.cards.domain.Card;
import org.junit.jupiter.api.Test;

class CardServiceTest {

    private final CardService service = new CardService();

    @Test
    void freezeAndUnfreezeFlow() {
        Card card = service.issueVirtualCard("user-1");
        service.freeze(card.getId());
        assertThat(service.find(card.getId())).isPresent().get().extracting(Card::getStatus).isEqualTo("FROZEN");
        service.unfreeze(card.getId());
        assertThat(service.find(card.getId())).isPresent().get().extracting(Card::getStatus).isEqualTo("ACTIVE");
    }
}
