package com.payment.personal.repository;

import com.payment.personal.models.replay.entity.ReplayEventEmbedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmbeddingRepository extends JpaRepository<ReplayEventEmbedding, Long> {
}
