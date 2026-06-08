package com.payment.personal.models.replay.request;

public record CreateEventRequest(
        String eventType,
        String content
) {
}
