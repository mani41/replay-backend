package com.payment.personal.models;

import lombok.Builder;

@Builder
public record ExtractTextResponse(
        String text,
        long characterCount,
        int pages
) {
}
