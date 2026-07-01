package com.payment.personal.service;

import com.payment.personal.models.ChunkResponse;
import com.payment.personal.models.ChunkText;
import com.payment.personal.models.ExtractTextResponse;
import com.payment.personal.models.replay.response.GeneratedReplayEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeService {

    private final DocumentChunker documentChunker;
    private final IntelligenceClient intelligenceClient;

    public ExtractTextResponse extractText(MultipartFile file) throws IOException {
        try (PDDocument pdDocument = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(pdDocument);
            return ExtractTextResponse.builder()
                    .text(text)
                    .characterCount(text.length())
                    .pages(pdDocument.getNumberOfPages())
                    .build();
        }
    }

    public ChunkResponse chunkText(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            text = text.replace("\r\n", "\n").trim();

            List<ChunkText> chunks = documentChunker.chunk(text);

            return ChunkResponse.builder()
                    .pageCount(document.getNumberOfPages())
                    .chunks(chunks)
                    .build();
        }
    }

    public GeneratedReplayEvents generateEvents(MultipartFile file) throws IOException {
        try(PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            text = text.replace("\r\n", "\n").trim();

            List<ChunkText> chunks = documentChunker.chunk(text);

            Set<String> stepAssembler = new LinkedHashSet<>();

            for(ChunkText chunk : chunks) {
                List<String> steps = intelligenceClient.generateSteps(chunk.content());
                log.info("steps generated, {}", steps);
                steps.forEach(step -> {
                    step = normalize(step);
                    stepAssembler.add(step);
                });
            }

            // use stepAssembler to produce final replay events
            return intelligenceClient.reorganizeSteps(stepAssembler);
        }
    }

    private String normalize(String step) {
        return step
                .trim()
                .replace(".", "")
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }
}
