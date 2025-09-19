package com.gigcred.cards.domain;

import java.time.Instant;

public class Card {
    private final String id;
    private final String userId;
    private final String token;
    private String status;
    private final Instant createdAt;

    public Card(String id, String userId, String token) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void freeze() {
        this.status = "FROZEN";
    }

    public void unfreeze() {
        this.status = "ACTIVE";
    }
}
