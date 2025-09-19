package com.gigcred.integrations.flutterwave;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.gigcred.integrations.flutterwave.dto.KycRequest;
import com.gigcred.integrations.flutterwave.dto.KycResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
class FlutterwaveClientFacadeTest {

    private static final WireMockServer wireMock = new WireMockServer(0);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        wireMock.start();
        registry.add("flutterwave.base-url", () -> "http://localhost:" + wireMock.port());
        registry.add("flutterwave.api-key", () -> "test-key");
        registry.add("flutterwave.webhook-secret", () -> "secret");
        registry.add("flutterwave.connect-timeout-ms", () -> 2000);
        registry.add("flutterwave.read-timeout-ms", () -> 2000);
    }

    @Autowired
    private FlutterwaveClientFacade facade;

    @BeforeEach
    void setup() {
        wireMock.resetAll();
    }

    @AfterEach
    void tearDown() {
        wireMock.resetAll();
    }

    @AfterAll
    static void shutdown() {
        wireMock.stop();
    }

    @Test
    void startKycHappyPath() {
        stubFor(post(urlEqualTo("/kyc/verify"))
            .withHeader("Authorization", equalTo("Bearer test-key"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{" +
                    "\"status\":\"success\"," +
                    "\"reference\":\"ref-123\"," +
                    "\"createdAt\":\"" + Instant.parse("2024-01-01T00:00:00Z") + "\"," +
                    "\"message\":\"processed\"}")));

        KycResponse response = facade.startKyc(new KycRequest("123", "Ada", "Lovelace", "+27123456789", "ada@example.com", List.of()));
        assertThat(response.reference()).isEqualTo("ref-123");
    }
}
