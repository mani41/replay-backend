package com.payment.personal.controllers;

import com.payment.personal.models.ExtractTextResponse;
import com.payment.personal.models.ImportKnowledgeRequest;
import com.payment.personal.models.replay.response.GeneratedReplayEvents;
import com.payment.personal.service.IntelligenceClient;
import com.payment.personal.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/replay/smart")
@RequiredArgsConstructor
public class IntelligentController {

    private final IntelligenceClient intelligenceClient;
    private final KnowledgeService knowledgeService;

    @GetMapping("/transcribe")
    public String test() {

        return intelligenceClient
                .transcribe(
                        "/Users/smart/work/intelligence-service/input/9f751580-f985-4493-86c1-8a41505ababd_voice.m4a");
    }

    // based on the topic only, it will generate replay event
    // TODO: Integrate with better LLM to get the best output
    @PostMapping("/generate-events")
    public GeneratedReplayEvents generateEvents(@RequestBody String topic) {
        return intelligenceClient.generateEvents(topic);
    }

    //
    @PostMapping("/knowledge/import")
    public GeneratedReplayEvents importKnowledge(
            @RequestBody ImportKnowledgeRequest importKnowledgeRequest) {
       return intelligenceClient.generateReplayEvents(importKnowledgeRequest);
    }

    @PostMapping(value = "/knowledge/extract",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExtractTextResponse> extractText(@RequestParam("file") MultipartFile file)
        throws IOException {
        return ResponseEntity.ok(knowledgeService.extractText(file));
    }

}
