package com.payment.personal.models;

import lombok.Builder;

@Builder
public record ChunkText(
        int chunkNo,
        int characterCount,
        String content
) {
}
