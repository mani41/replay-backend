package com.payment.personal.models;

import lombok.Builder;

import java.util.List;

@Builder
public record ChunkResponse(
        int pageCount,
        List<ChunkText> chunks
) {
}
