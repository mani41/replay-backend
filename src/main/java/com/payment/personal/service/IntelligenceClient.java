package com.payment.personal.service;

import com.payment.personal.models.intelli.TranscriptRequest;
import com.payment.personal.models.intelli.TranscriptResponse;
import com.payment.personal.models.replay.response.GeneratedReplayEvents;
import com.payment.personal.models.replay.response.SummaryRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class IntelligenceClient {

    @Value("${app.storage.root}")
    private String storageRoot;

    private final RestClient restClient;

    public IntelligenceClient() {
        this.restClient = RestClient.builder()
                        .baseUrl("http://localhost:8000")
                        .build();
    }

    public String transcribe(
            String filePath) {

        Path voiceDir = Paths.get(storageRoot, "voice");
        Path targetFile = voiceDir.resolve(filePath);

        TranscriptResponse response =
                restClient.post()
                        .uri("/transcribe")
                        .body(
                                new TranscriptRequest(targetFile
                                        .toAbsolutePath()
                                        .toString())
                        )
                        .retrieve()
                        .body(
                                TranscriptResponse.class
                        );

        assert response != null;
        return response.transcript();
    }

    public GeneratedReplayEvents generateEvents(String topic) {

        return restClient.post()
                .uri("/generate-events")
                .body(
                        new SummaryRequest(topic)
                )
                .retrieve()
                .body(
                        GeneratedReplayEvents.class
                );
    }
}
