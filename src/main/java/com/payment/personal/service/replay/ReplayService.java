package com.payment.personal.service.replay;

import com.payment.personal.auth.dto.User;
import com.payment.personal.models.UpdateEventRequest;
import com.payment.personal.models.replay.dto.CreateReplayRequest;
import com.payment.personal.models.replay.entity.Replay;
import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.request.CreateEventRequest;
import com.payment.personal.models.replay.response.GeneratedReplayEvents;
import com.payment.personal.models.replay.response.ReplayEventResponse;
import com.payment.personal.models.replay.response.ReplayResponse;
import com.payment.personal.models.replay.response.SearchResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
public interface ReplayService {

    ReplayResponse createReplay(
            CreateReplayRequest request) throws AccessDeniedException;

    List<ReplayResponse> getAllReplays(UUID userId);

    ReplayResponse getReplay(Long replayId);

    void addNote(
            Long replayId,
            CreateEventRequest request);

    List<ReplayEventResponse> getEvents(
            Long replayId);

    void uploadPhoto(
            Long replayId,
            MultipartFile file);

    void uploadVoice(
            Long replayId,
            MultipartFile file);

    void deleteEvent(Long eventId);

    ReplayEvent updateEvent(Long eventId, UpdateEventRequest request);

    List<SearchResult> search(String q);

    void updateReplay(Long replayId, Replay replay);

    void deleteReplay(Long replayId);

    void saveGeneratedReplayEvent(GeneratedReplayEvents generatedReplayEvent);

    List<ReplayEventResponse> searchSemantics(String request);
}
