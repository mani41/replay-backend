package com.payment.personal.service.embedding;

import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.entity.ReplayEventEmbedding;
import com.payment.personal.models.replay.request.EmbeddingRequest;
import com.payment.personal.repository.EmbeddingRepository;
import com.payment.personal.service.IntelligenceClient;
import com.pgvector.PGvector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmbeddingServiceImpl implements EmbeddingService {

    private final IntelligenceClient intelligenceClient;
    private final JdbcTemplate jdbcTemplate;


    @Override
    public double[] embed(EmbeddingRequest embeddingRequest) {
        return intelligenceClient.createEmbedding(embeddingRequest.content());
    }

    @Override
    @Async
    public void generateEmbeddingAsync(ReplayEvent savedReplayEvent) {
        generateAndSave(savedReplayEvent);
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
        String vectorStr = Arrays.stream(vector)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(",", "[", "]"));

        jdbcTemplate.update("""
                        INSERT INTO replay_event_embedding
                        (
                            replay_event_id,
                            embedding,
                            model,
                            created_at
                        )
                        VALUES
                        (
                            ?,
                            ?::vector,
                            ?,
                            ?
                        )
                        """,
                event.getId(),
                vectorStr,
                "nomic-embed-text",
                LocalDateTime.now());
//
//        embeddingRepository.save(
//                ReplayEventEmbedding.builder()
//                        .replayEventId(event.getId())
//                        .createdAt(LocalDateTime.now())
//                        .embedding(new PGvector(vector))
//                        .model("nomic-embed-text")
//                        .build()
//        );
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
