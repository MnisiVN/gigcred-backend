package com.gigcred.onboarding.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OnboardingRecord {
    private final String userId;
    private String status;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<String> documentRefs = new ArrayList<>();
    private String providerReference;

    public OnboardingRecord(String userId) {
        this.userId = userId;
        this.status = "PENDING";
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<String> getDocumentRefs() {
        return Collections.unmodifiableList(documentRefs);
    }

    public String getProviderReference() {
        return providerReference;
    }

    public void addDocument(String reference) {
        documentRefs.add(reference);
        touch();
    }

    public void markSubmitted(String providerReference) {
        this.providerReference = providerReference;
        this.status = "IN_REVIEW";
        touch();
    }

    public void markStatus(String status) {
        this.status = status;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}
