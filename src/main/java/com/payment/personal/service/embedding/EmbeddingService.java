package com.payment.personal.service.embedding;

import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.request.EmbeddingRequest;

interface EmbeddingService {
    double[] embed(EmbeddingRequest embeddingRequest);

    void generateEmbeddingAsync(ReplayEvent savedReplayEvent);
}
