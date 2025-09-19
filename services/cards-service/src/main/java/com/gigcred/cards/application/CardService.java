package com.gigcred.cards.application;

import com.gigcred.cards.domain.Card;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    private final Map<String, Card> cards = new ConcurrentHashMap<>();

    public Card issueVirtualCard(String userId) {
        Card card = new Card(UUID.randomUUID().toString(), userId, UUID.randomUUID().toString());
        cards.put(card.getId(), card);
        return card;
    }

    public Optional<Card> find(String cardId) {
        return Optional.ofNullable(cards.get(cardId));
    }

    public List<Card> listForUser(String userId) {
        return cards.values().stream().filter(card -> card.getUserId().equals(userId)).toList();
    }

    public Optional<Card> freeze(String cardId) {
        return find(cardId).map(card -> {
            card.freeze();
            return card;
        });
    }

    public Optional<Card> unfreeze(String cardId) {
        return find(cardId).map(card -> {
            card.unfreeze();
            return card;
        });
    }
}
