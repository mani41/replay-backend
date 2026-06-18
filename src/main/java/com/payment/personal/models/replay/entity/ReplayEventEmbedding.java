package com.payment.personal.models.replay.entity;

import com.pgvector.PGvector;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "replay_event_embedding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplayEventEmbedding {

    @Id
    private Long replayEventId;

    private String model;

    @Column(columnDefinition = "vector(768)")
    private PGvector embedding;

    private LocalDateTime createdAt;

}
