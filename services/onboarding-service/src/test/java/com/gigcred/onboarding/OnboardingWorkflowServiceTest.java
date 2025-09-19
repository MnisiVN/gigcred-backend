package com.gigcred.onboarding;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gigcred.integrations.flutterwave.FlutterwaveClientFacade;
import com.gigcred.integrations.flutterwave.dto.KycRequest;
import com.gigcred.integrations.flutterwave.dto.KycResponse;
import com.gigcred.onboarding.application.OnboardingWorkflowService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OnboardingWorkflowServiceTest {

    @Mock
    private FlutterwaveClientFacade flutterwaveClientFacade;

    private OnboardingWorkflowService workflowService;

    @BeforeEach
    void setUp() {
        workflowService = new OnboardingWorkflowService(flutterwaveClientFacade);
    }

    @Test
    void transitionsThroughWebhook() {
        when(flutterwaveClientFacade.startKyc(any())).thenReturn(new KycResponse("pending", "ref-1", Instant.now(), "submitted"));

        workflowService.start("user-1", new KycRequest("bvn", "Ada", "Lovelace", "+2712345678", "ada@example.com", List.of()));
        workflowService.handleWebhook("user-1", "APPROVED");

        assertThat(workflowService.find("user-1")).isPresent()
            .get()
            .extracting(record -> record.getStatus())
            .isEqualTo("APPROVED");
    }
}
