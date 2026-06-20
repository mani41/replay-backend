package com.payment.personal.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EmbeddingRepositoryUtility {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> findNearestEvents(
            double[] embedding,
            int limit) {

        String vector =
                Arrays.stream(embedding)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(
                                ",",
                                "[",
                                "]"));

        return jdbcTemplate.query(
                """
                        SELECT replay_event_id
                        FROM replay_event_embedding
                        ORDER BY embedding <=> CAST(? AS vector)
                        LIMIT ?
                        """,
                (rs, rowNum) ->
                        rs.getLong(
                                "replay_event_id"),
                vector,
                limit
        );
    }

    public void cosineDistance(double[] embedding, int limit) {
        String vector =
                Arrays.stream(embedding)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(
                                ",",
                                "[",
                                "]"));

        List<Object> distances = jdbcTemplate.query(
                """
                        SELECT replay_event_id, embedding <=> CAST(? AS vector) AS distance
                        FROM replay_event_embedding
                        ORDER BY distance
                        LIMIT ?
                        """,
                (rs, rowNum) ->
                        rs.getObject(
                                "distance"),
                vector,
                limit
        );

        log.info("distance: {}", distances);
    }

    public void generateAndSave(double[] vector, Long eventId) {
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
                eventId,
                vectorStr,
                "nomic-embed-text",
                LocalDateTime.now());
    }
}
