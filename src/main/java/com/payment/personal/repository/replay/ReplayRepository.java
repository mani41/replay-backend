package com.payment.personal.repository.replay;

import com.payment.personal.models.replay.entity.Replay;
import com.payment.personal.models.replay.response.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplayRepository extends JpaRepository<Replay, Long> {

    @Query("""
            SELECT new com.payment.personal.models.replay.response.SearchResult(
                r.id,
                e.id,
                r.title,
                e.eventType,
                e.content
            )
            FROM ReplayEvent e
            JOIN Replay r
            ON r.id = e.replayId
            WHERE LOWER(e.content)
            LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    List<SearchResult> search(@Param("query") String query);
}
