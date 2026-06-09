package com.payment.personal.service;

import com.payment.personal.models.intelli.TranscriptRequest;
import com.payment.personal.models.intelli.TranscriptResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IntelligenceClient {

    private final RestClient restClient;

    public IntelligenceClient() {

        this.restClient =
                RestClient.builder()
                        .baseUrl(
                                "http://localhost:8000")
                        .build();
    }

    public String transcribe(
            String filePath) {

        TranscriptResponse response =
                restClient.post()
                        .uri("/transcribe")
                        .body(
                                new TranscriptRequest(
                                        filePath))
                        .retrieve()
                        .body(
                                TranscriptResponse.class);

        assert response != null;
        return response.transcript();
    }
}
