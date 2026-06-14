package com.payment.personal.models.replay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "replay_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplayEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long replayId;

    private String eventType;

    @Column(length = 10000)
    private String content;

    private String filePath;

    private LocalDateTime createdAt;

    private String title;

    private Integer eventOrder;
}
