package com.payment.personal.models.replay.response;

public record SearchResult(
        Long replayId,
        Long eventId,
        String replayTitle,
        String eventType,
        String content
) {
}
