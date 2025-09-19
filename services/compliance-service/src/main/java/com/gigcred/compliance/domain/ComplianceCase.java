package com.gigcred.compliance.domain;

import java.time.Instant;

public record ComplianceCase(String caseId, String userId, String reason, String status, Instant createdAt) {
}
