package com.payment.personal.service.replay.impl;

import com.payment.personal.auth.dto.User;
import com.payment.personal.auth.service.AuthenticationService;
import com.payment.personal.models.UpdateEventRequest;
import com.payment.personal.models.replay.dto.CreateReplayRequest;
import com.payment.personal.models.replay.entity.Replay;
import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.request.CreateEventRequest;
import com.payment.personal.models.replay.request.EmbeddingRequest;
import com.payment.personal.models.replay.response.GeneratedReplayEvents;
import com.payment.personal.models.replay.response.ReplayEventResponse;
import com.payment.personal.models.replay.response.ReplayResponse;
import com.payment.personal.models.replay.response.SearchResult;
import com.payment.personal.repository.replay.ReplayEventRepository;
import com.payment.personal.repository.replay.ReplayRepository;
import com.payment.personal.service.IntelligenceClient;
import com.payment.personal.service.embedding.EmbeddingServiceImpl;
import com.payment.personal.service.replay.ReplayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReplayServiceImpl implements ReplayService {

    private final ReplayRepository replayRepository;
    private final ReplayEventRepository replayEventRepository;
    private final IntelligenceClient intelligenceClient;
    private final ObjectMapper objectMapper;
    private final EmbeddingServiceImpl embeddingService;
    private final AuthenticationService authenticationService;

    @Value("${app.storage.root}")
    private String storageRoot;

    @Override
    public ReplayResponse createReplay(
            CreateReplayRequest request) throws AccessDeniedException {

        User user = authenticationService.getCurrentUser();
        Replay replay = Replay.builder()
                .title(request.title())
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        replay = replayRepository.save(replay);

        List<String> tags = null;
        if (replay.getTags() != null) {
            tags = objectMapper.readValue(
                    replay.getTags(),
                    new TypeReference<>() {
                    });
        }

        return new ReplayResponse(
                replay.getId(),
                replay.getTitle(),
                replay.getDescription(),
                tags
        );
    }

    @Override
    public List<ReplayResponse> getAllReplays(UUID userId) {

        return replayRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(replay -> {
                            List<String> tags = List.of();

                            try {
                                if (replay.getTags() != null) {
                                    tags = objectMapper.readValue(
                                            replay.getTags(),
                                            new TypeReference<>() {
                                            });
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(
                                        "Failed to parse tags",
                                        e);
                            }

                            return new ReplayResponse(
                                    replay.getId(),
                                    replay.getTitle(),
                                    replay.getDescription(),
                                    tags
                            );
                        }
                )
                .toList();
    }

    @Override
    public ReplayResponse getReplay(Long replayId) {

        Replay replay = replayRepository
                .findById(replayId)
                .orElseThrow(() -> new RuntimeException(
                        "Replay not found"));

        List<String> tags =
                objectMapper.readValue(
                        replay.getTags(),
                        new TypeReference<>() {
                        });

        return new ReplayResponse(
                replay.getId(),
                replay.getTitle(),
                replay.getDescription(),
                tags
        );
    }

    @Override
    public void addNote(
            Long replayId,
            CreateEventRequest request) {

        validateReplay(replayId);

        ReplayEvent event = ReplayEvent.builder()
                .replayId(replayId)
                .eventType("NOTE")
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .build();

        //replayEventRepository.save(event);
        saveReplayEvent(event);
    }

    @Override
    public List<ReplayEventResponse> getEvents(
            Long replayId) {

        return replayEventRepository
                .findByReplayIdOrderByCreatedAtAsc(
                        replayId)
                .stream()
                .map(event ->
                        new ReplayEventResponse(
                                event.getId(),
                                event.getEventType(),
                                event.getContent(),
                                event.getFilePath(),
                                event.getCreatedAt(),
                                event.getEventOrder(),
                                event.getTitle()
                        ))
                .toList();
    }

    @Override
    public void uploadPhoto(
            Long replayId,
            MultipartFile file) {

        validateReplay(replayId);

        String filePath =
                saveFile(file, "photos");

        ReplayEvent event = ReplayEvent.builder()
                .replayId(replayId)
                .eventType("PHOTO")
                .filePath(filePath)
                .createdAt(LocalDateTime.now())
                .build();

        replayEventRepository.save(event);
    }

    @Override
    public void uploadVoice(Long replayId, MultipartFile file) {

        validateReplay(replayId);

        String filePath = saveFile(file, "voice");

        String transcript = "";
        try {
            transcript = intelligenceClient.transcribe(filePath);
        } catch (Exception e) {
            log.error("Transcription Failed", e);
        }

        ReplayEvent event = ReplayEvent.builder()
                .replayId(replayId)
                .eventType("VOICE")
                .filePath(filePath)
                .createdAt(LocalDateTime.now())
                .content(transcript)
                .build();

        //replayEventRepository.save(event);
        saveReplayEvent(event);
    }

    @Override
    public void deleteEvent(Long eventId) {
        ReplayEvent replayEvent = replayEventRepository.getReferenceById(eventId);

        try {
            if (replayEvent.getFilePath() != null) {
                File file = new File(replayEvent.getFilePath());
                Files.delete(file.toPath());
            }
            replayEventRepository.deleteById(eventId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ReplayEvent updateEvent(Long eventId, UpdateEventRequest request) {
        ReplayEvent event =
                replayEventRepository
                        .findById(eventId)
                        .orElseThrow();

        event.setContent(
                request.content());

        //return replayEventRepository.save(event);
        return saveReplayEvent(event);
    }

    @Override
    public List<SearchResult> search(String q) {

        return replayRepository.search(q);
    }

    @Override
    public void updateReplay(Long replayId, Replay replay) {
        Replay dbReplay = replayRepository.getReferenceById(replayId);
        if (!Objects.equals(dbReplay.getDescription(), replay.getDescription())
                || !Objects.equals(dbReplay.getTitle(), replay.getTitle())) {
            dbReplay.setTitle(replay.getTitle());
            dbReplay.setDescription(replay.getDescription());
        }
    }

    @Override
    public void deleteReplay(Long replayId) {
        //TODO: Remove photos or voice related to this replay
        List<ReplayEvent> events = replayEventRepository.findByReplayId(replayId);
        for (ReplayEvent event : events) {

            if (event.getFilePath() != null) {
                try {
                    Files.deleteIfExists(
                            Paths.get(
                                    storageRoot,
                                    event.getFilePath()
                            )
                    );
                } catch (IOException e) {
                    log.error("Could delete file for event: {}, Reason: {}", event.getId(), e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }

        replayEventRepository.deleteByReplayId(replayId);
        replayRepository.deleteById(replayId);
    }

    @Override
    public void saveGeneratedReplayEvent(GeneratedReplayEvents generatedReplayEvent) {
        Replay replay = Replay.builder()
                .title(generatedReplayEvent.title())
                .description(generatedReplayEvent.summary())
                .tags(objectMapper.writeValueAsString(generatedReplayEvent.tags()))
                .build();

        Replay savedReplay = replayRepository.save(replay);

        AtomicInteger orderNo = new AtomicInteger(1);

        List<ReplayEvent> replayEvents =
                generatedReplayEvent.steps().stream()
                        .map(step ->
                                ReplayEvent.builder()
                                        .replayId(savedReplay.getId())
                                        .content(step.description())
                                        .eventOrder(orderNo.getAndIncrement())
                                        .eventType("STEP")
                                        .title(step.title())
                                        .build()
                        ).toList();

        List<ReplayEvent> savedReplayEvents = replayEventRepository.saveAll(replayEvents);
        embeddingService.generateEmbeddingsAsync(savedReplayEvents);

    }

    @Override
    public List<ReplayEventResponse> searchSemantics(String query) {

        double[] embeddings = embeddingService.embed(new EmbeddingRequest(query));

        List<Long> eventIds =
                embeddingService.findNearestEvents(embeddings, 5);

        Map<Long, ReplayEvent> eventMap =
                replayEventRepository
                        .findAllById(eventIds)
                        .stream()
                        .collect(Collectors.toMap(
                                ReplayEvent::getId,
                                Function.identity()));

        return eventIds.stream()
                .map(eventMap::get)
                .filter(Objects::nonNull)
                .map(event -> new ReplayEventResponse(
                                event.getId(),
                                event.getEventType(),
                                event.getContent(),
                                event.getFilePath(),
                                event.getCreatedAt(),
                                event.getEventOrder(),
                                event.getTitle()
                        )
                )
                .toList();
    }

    private void validateReplay(Long replayId) {
        if (!replayRepository.existsById(replayId)) {
            throw new RuntimeException("Replay not found");
        }
    }

    private String saveFile(MultipartFile file, String folder) {
        try {
            Path directory = Paths.get(storageRoot, folder).toAbsolutePath();
            Files.createDirectories(directory);
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = directory.resolve(fileName);
            Files.copy(file.getInputStream(), target);
            return target.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to save file", ex);
        }
    }

    /*
    All replay event has to go through this async vector generation on 756 dimensions
     */
    private ReplayEvent saveReplayEvent(ReplayEvent replayEvent) {
        ReplayEvent savedReplayEvent = replayEventRepository.save(replayEvent);
        embeddingService.generateEmbeddingAsync(savedReplayEvent);
        return savedReplayEvent;
    }
}
