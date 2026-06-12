package com.payment.personal.service;

import com.payment.personal.models.intelli.TranscriptRequest;
import com.payment.personal.models.intelli.TranscriptResponse;
import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.response.SummaryRequest;
import com.payment.personal.models.replay.response.SummaryResponse;
import com.payment.personal.repository.replay.ReplayEventRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class IntelligenceClient {

    @Value("${app.storage.root}")
    private String storageRoot;

    private final RestClient restClient;
    private final ReplayEventRepository replayEventRepository;

    public IntelligenceClient(ReplayEventRepository replayEventRepository) {
        this.replayEventRepository = replayEventRepository;
        this.restClient = RestClient.builder()
                        .baseUrl("http://localhost:8000")
                        .build();
    }

    public String transcribe(
            String filePath) {

        Path voiceDir = Paths.get(storageRoot, "voice");
        Path targetFile = voiceDir.resolve(filePath);

        TranscriptResponse response =
                restClient.post()
                        .uri("/transcribe")
                        .body(
                                new TranscriptRequest(targetFile
                                        .toAbsolutePath()
                                        .toString())
                        )
                        .retrieve()
                        .body(
                                TranscriptResponse.class
                        );

        assert response != null;
        return response.transcript();
    }

    public String summarize(Long replayId) {
        List<ReplayEvent> replayEventList =
                replayEventRepository.findByReplayIdOrderByCreatedAtAsc(replayId);

        String masterTranscript = IntStream.range(0, replayEventList.size())
                .mapToObj(i -> "Step" + (i + 1) + ": " + replayEventList.get(i).getContent())
                .collect(Collectors.joining(", "));

        SummaryResponse summaryResponse =
                restClient.post()
                        .uri("/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new SummaryRequest(masterTranscript))
                        .retrieve()
                        .body(SummaryResponse.class);
        assert summaryResponse != null;
        return summaryResponse.summary() != null ? summaryResponse.summary() : "No Summary";
    }
}
