package com.gigcred.compliance;

import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.compliance.application.ComplianceService;
import org.junit.jupiter.api.Test;

class ComplianceServiceTest {

    private final ComplianceService service = new ComplianceService();

    @Test
    void screeningCreatesCase() {
        var complianceCase = service.runScreening("user-1", true, 0);
        assertThat(complianceCase.status()).isEqualTo("REVIEW");
        assertThat(service.find(complianceCase.caseId())).isPresent();
    }
}
