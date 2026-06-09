package com.payment.personal.service.replay.impl;

import com.payment.personal.models.replay.dto.CreateReplayRequest;
import com.payment.personal.models.replay.entity.Replay;
import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.request.CreateEventRequest;
import com.payment.personal.models.replay.response.ReplayEventResponse;
import com.payment.personal.models.replay.response.ReplayResponse;
import com.payment.personal.repository.replay.ReplayEventRepository;
import com.payment.personal.repository.replay.ReplayRepository;
import com.payment.personal.service.replay.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplayServiceImpl implements ReplayService {

    private final ReplayRepository replayRepository;
    private final ReplayEventRepository replayEventRepository;

    @Override
    public ReplayResponse createReplay(
            CreateReplayRequest request) {

        Replay replay = Replay.builder()
                .title(request.title())
                .description(request.description())
                .createdAt(LocalDateTime.now())
                .build();

        replay = replayRepository.save(replay);

        return new ReplayResponse(
                replay.getId(),
                replay.getTitle(),
                replay.getDescription()
        );
    }

    @Override
    public List<ReplayResponse> getAllReplays() {

        return replayRepository.findAll()
                .stream()
                .map(replay -> new ReplayResponse(
                        replay.getId(),
                        replay.getTitle(),
                        replay.getDescription()
                ))
                .toList();
    }

    @Override
    public ReplayResponse getReplay(Long replayId) {

        Replay replay = replayRepository
                .findById(replayId)
                .orElseThrow(() -> new RuntimeException(
                                "Replay not found"));

        return new ReplayResponse(
                replay.getId(),
                replay.getTitle(),
                replay.getDescription()
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

        replayEventRepository.save(event);
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
                                event.getCreatedAt()
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

        ReplayEvent event = ReplayEvent.builder()
                .replayId(replayId)
                .eventType("VOICE")
                .filePath(filePath)
                .createdAt(LocalDateTime.now())
                .build();

        replayEventRepository.save(event);
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

    private void validateReplay(Long replayId) {
        if (!replayRepository.existsById(replayId)) {
            throw new RuntimeException("Replay not found");
        }
    }

    private String saveFile(MultipartFile file, String folder) {
        try {
            Path directory = Paths.get("uploads", folder);
            Files.createDirectories(directory);
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path target = directory.resolve(fileName);
            Files.copy(file.getInputStream(), target);
            return target.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to save file", ex);
        }
    }
}
