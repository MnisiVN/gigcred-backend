package com.gigcred.onboarding.application;

import com.gigcred.integrations.flutterwave.FlutterwaveClientFacade;
import com.gigcred.integrations.flutterwave.dto.KycRequest;
import com.gigcred.integrations.flutterwave.dto.KycResponse;
import com.gigcred.onboarding.domain.OnboardingRecord;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class OnboardingWorkflowService {

    private final FlutterwaveClientFacade flutterwaveClient;
    private final Map<String, OnboardingRecord> records = new ConcurrentHashMap<>();

    public OnboardingWorkflowService(FlutterwaveClientFacade flutterwaveClient) {
        this.flutterwaveClient = flutterwaveClient;
    }

    public OnboardingRecord start(String userId, KycRequest request) {
        OnboardingRecord record = records.computeIfAbsent(userId, OnboardingRecord::new);
        KycResponse response = flutterwaveClient.startKyc(request);
        record.markSubmitted(response.reference());
        return record;
    }

    public OnboardingRecord uploadDocument(String userId, String documentReference) {
        OnboardingRecord record = records.computeIfAbsent(userId, OnboardingRecord::new);
        record.addDocument(documentReference);
        return record;
    }

    public Optional<OnboardingRecord> find(String userId) {
        return Optional.ofNullable(records.get(userId));
    }

    public void handleWebhook(String userId, String status) {
        records.computeIfAbsent(userId, OnboardingRecord::new).markStatus(status);
    }
}
