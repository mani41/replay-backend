package com.payment.personal.service.replay;

import com.payment.personal.models.replay.dto.CreateReplayRequest;
import com.payment.personal.models.replay.request.CreateEventRequest;
import com.payment.personal.models.replay.response.ReplayEventResponse;
import com.payment.personal.models.replay.response.ReplayResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ReplayService {

    ReplayResponse createReplay(
            CreateReplayRequest request);

    List<ReplayResponse> getAllReplays();

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
}
