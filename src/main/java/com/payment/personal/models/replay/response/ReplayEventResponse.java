package com.payment.personal.models.replay.response;

public record ReplayEventResponse(
        Long id,
        String eventType,
        String content,
        String filePath,
        java.time.LocalDateTime createdAt,
        Integer eventOrder) {
}
