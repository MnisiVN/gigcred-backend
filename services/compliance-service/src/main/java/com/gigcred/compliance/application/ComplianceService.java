package com.gigcred.compliance.application;

import com.gigcred.compliance.domain.ComplianceCase;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ComplianceService {

    private final Map<String, ComplianceCase> cases = new ConcurrentHashMap<>();

    public ComplianceCase runScreening(String userId, boolean pepMatch, int velocityAlerts) {
        String status = pepMatch || velocityAlerts > 0 ? "REVIEW" : "CLEARED";
        ComplianceCase complianceCase = new ComplianceCase(UUID.randomUUID().toString(), userId, pepMatch ? "PEP" : "AUTOMATED", status, Instant.now());
        cases.put(complianceCase.caseId(), complianceCase);
        return complianceCase;
    }

    public Optional<ComplianceCase> find(String caseId) {
        return Optional.ofNullable(cases.get(caseId));
    }
}
