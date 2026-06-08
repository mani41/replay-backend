package com.payment.personal.repository.replay;

import com.payment.personal.models.replay.entity.Replay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplayRepository extends JpaRepository<Replay, Long> {

}
