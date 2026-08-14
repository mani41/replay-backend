package com.payment.personal.service;

import com.payment.personal.models.ImportKnowledgeRequest;
import com.payment.personal.models.intelli.ChunkRequest;
import com.payment.personal.models.intelli.ReplayRequest;
import com.payment.personal.models.intelli.TranscriptRequest;
import com.payment.personal.models.intelli.TranscriptResponse;
import com.payment.personal.models.replay.request.EmbeddingRequest;
import com.payment.personal.models.replay.response.GeneratedReplayEvents;
import com.payment.personal.models.replay.request.SummaryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
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

        try {
            return restClient.post()
                    .uri("/generate-events")
                    .body(
                            new SummaryRequest(topic)
                    )
                    .retrieve()
                    .body(
                            GeneratedReplayEvents.class
                    );
        } catch (RestClientException ex) {
            log.error("Failed to generate events from topic", ex);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Intelligence Service may be unavailable to generate events from topic",
                    ex
            );
        }
    }

    public double[] createEmbedding(String content) {
        try {
            return restClient.post()
                    .uri("/embedding")
                    .body(new EmbeddingRequest(content))
                    .retrieve()
                    .body(double[].class);

        } catch (RestClientException ex) {
            log.error("Failed to generate embedding", ex);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Embedding service is unavailable",
                    ex
            );
        }
    }


    public GeneratedReplayEvents generateReplayEvents(ImportKnowledgeRequest importKnowledgeRequest) {
        try {
            return restClient.post()
                    .uri("/generate-events/import")
                    .body(new SummaryRequest(importKnowledgeRequest.text()))
                    .retrieve()
                    .body(GeneratedReplayEvents.class);
        } catch (RestClientException ex) {
            log.error("Failed to generate replay events from import", ex);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Intelligence service may be unavailable to generate replay events",
                    ex
            );
        }
    }

    // these steps are generated based on chunks
    public List<String> generateSteps(String chunk) {

        try {
            return restClient.post()
                    .uri("/generate-events/steps")
                    .body(new ChunkRequest(chunk))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<String>>() {});
        } catch (RestClientException ex) {
            log.error("Failed to generate events steps from chunk", ex);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Intelligence service may be unavailable to generate steps from chunk",
                    ex
            );
        }
    }

    // generated steps produced by LLM will be reorganized here
    public GeneratedReplayEvents reorganizeSteps(Set<String> stepAssembler) {

        ReplayRequest request = new ReplayRequest(new ArrayList<>(stepAssembler));

        try {
            return restClient.post()
                    .uri("/generate-event/reorganize")
                    .body(request)
                    .retrieve()
                    .body(
                            GeneratedReplayEvents.class
                    );
        } catch (RestClientException ex) {
            log.error("Failed to reorganize events from steps", ex);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Intelligence service may be unavailable to re-organize events from steps",
                    ex
            );
        }

    }
}
