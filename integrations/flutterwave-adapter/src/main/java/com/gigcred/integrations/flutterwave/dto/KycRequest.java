package com.gigcred.integrations.flutterwave.dto;

import java.util.List;

public record KycRequest(
    String bvn,
    String firstName,
    String lastName,
    String phoneNumber,
    String email,
    List<Document> documents
) {
    public record Document(String type, String content, String filename) {}
}
