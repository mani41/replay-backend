package com.payment.personal.service.embedding;

import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.request.EmbeddingRequest;
import com.payment.personal.repository.EmbeddingRepositoryUtility;
import com.payment.personal.service.IntelligenceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    private final IntelligenceClient intelligenceClient;
    private final EmbeddingRepositoryUtility embeddingRepositoryUtility;


    @Override
    public double[] embed(EmbeddingRequest embeddingRequest) {
        return intelligenceClient.createEmbedding(embeddingRequest.content());
    }

    @Override
    @Async
    public void generateEmbeddingAsync(ReplayEvent savedReplayEvent) {
        generateAndSave(savedReplayEvent);
    }

    @Override
    public List<Long> findNearestEvents(double[] embeddings, int limit) {
        embeddingRepositoryUtility.cosineDistance(embeddings, limit);
        return embeddingRepositoryUtility.findNearestEvents(embeddings, limit);
    }

    @Async
    public void generateEmbeddingsAsync(List<ReplayEvent> events) {
        for (ReplayEvent event : events) {
            try {
                generateAndSave(event);
            } catch (Exception ex) {
                log.error("Embedding failed", ex);
            }
        }
    }

    private void generateAndSave(ReplayEvent event) {
        String text = buildEmbeddingText(event);
        double[] vector = intelligenceClient.createEmbedding(text);
        embeddingRepositoryUtility.generateAndSave(vector, event.getId());
    }

    private String buildEmbeddingText(
            ReplayEvent event) {

        return """
                %s
                
                %s
                """.formatted(
                Optional.ofNullable(
                                event.getTitle())
                        .orElse(""),

                Optional.ofNullable(
                                event.getContent())
                        .orElse(""));
    }
}
