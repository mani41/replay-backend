package com.payment.personal.service.embedding;

import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.request.EmbeddingRequest;

import java.util.List;

interface EmbeddingService {
    double[] embed(EmbeddingRequest embeddingRequest);

    void generateEmbeddingAsync(ReplayEvent savedReplayEvent);

    List<Long> findNearestEvents(double[] embeddings, int limit);
}
