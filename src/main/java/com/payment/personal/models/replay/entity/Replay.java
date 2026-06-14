package com.payment.personal.models.replay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "replay")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Replay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 5000)
    private String description;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String tags;
}
