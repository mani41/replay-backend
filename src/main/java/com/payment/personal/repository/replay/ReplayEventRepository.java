package com.payment.personal.repository.replay;

import com.payment.personal.models.replay.entity.ReplayEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplayEventRepository extends JpaRepository<ReplayEvent, Long> {
    List<ReplayEvent> findByReplayIdOrderByCreatedAtAsc(
            Long replayId);

    void deleteByReplayId(Long replayId);

    List<ReplayEvent> findByReplayId(Long replayId);
}
