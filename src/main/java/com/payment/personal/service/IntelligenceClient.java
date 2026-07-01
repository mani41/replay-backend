package com.payment.personal.service;

import com.payment.personal.models.ImportKnowledgeRequest;
import com.payment.personal.models.intelli.ChunkRequest;
import com.payment.personal.models.intelli.ReplayRequest;
import com.payment.personal.models.intelli.TranscriptRequest;
import com.payment.personal.models.intelli.TranscriptResponse;
import com.payment.personal.models.replay.request.EmbeddingRequest;
import com.payment.personal.models.replay.response.GeneratedReplayEvents;
import com.payment.personal.models.replay.request.SummaryRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public double[] createEmbedding(String content) {
        return restClient.post()
                .uri("/embedding")
                .body(new EmbeddingRequest(content))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public GeneratedReplayEvents generateReplayEvents(ImportKnowledgeRequest importKnowledgeRequest) {
        return restClient.post()
                .uri("/generate-events/import")
                .body(new SummaryRequest(importKnowledgeRequest.text()))
                .retrieve()
                .body(GeneratedReplayEvents.class);
    }

    // these steps are generated based on chunks
    public List<String> generateSteps(String chunk) {

        return restClient.post()
                .uri("/generate-events/steps")
                .body(new ChunkRequest(chunk))
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    // generated steps produced by LLM will be reorganized here
    public GeneratedReplayEvents reorganizeSteps(Set<String> stepAssembler) {

        ReplayRequest request = new ReplayRequest(new ArrayList<>(stepAssembler));

        return restClient.post()
                .uri("/generate-event/reorganize")
                .body(request)
                .retrieve()
                .body(
                        GeneratedReplayEvents.class
                );

    }
}
