package com.payment.personal.models.replay.response;

import java.util.List;

public record ReplayResponse(
        Long id,
        String title,
        String description,
        List<String> tags
) {
}