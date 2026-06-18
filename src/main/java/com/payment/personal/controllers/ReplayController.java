package com.payment.personal.controllers;

import com.payment.personal.models.UpdateEventRequest;
import com.payment.personal.models.replay.dto.CreateReplayRequest;
import com.payment.personal.models.replay.entity.Replay;
import com.payment.personal.models.replay.entity.ReplayEvent;
import com.payment.personal.models.replay.request.CreateEventRequest;
import com.payment.personal.models.replay.response.GeneratedReplayEvents;
import com.payment.personal.models.replay.response.ReplayEventResponse;
import com.payment.personal.models.replay.response.ReplayResponse;
import com.payment.personal.models.replay.response.SearchResult;
import com.payment.personal.service.replay.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/replays")
@RequiredArgsConstructor
public class ReplayController {

    /**
     * this is constructor injection supported by lombok instead of
     * {@code @Autowired} field injection. Advantages
     * 1. Immutability
     * 2. NPE save when cotext is not initialized but this class is loaded before
     * 3. Early warning sign for single responsibility principle
     * 4. Easy unit test. No need of reflection or Inject mock framework
     */
    private final ReplayService replayService;

    @GetMapping
    public List<ReplayResponse> getReplays() {
        return replayService.getAllReplays();
    }

    @PostMapping
    public ReplayResponse createReplay(
            @RequestBody CreateReplayRequest request) {
        return replayService.createReplay(request);
    }

    @PostMapping("/create-gen-events")
    public ResponseEntity<String> createGeneratedReplayEvents(@RequestBody GeneratedReplayEvents generatedReplayEvent) {
        replayService.saveGeneratedReplayEvent(generatedReplayEvent);
        return ResponseEntity.ok("Replay Event Created");
    }

    @PostMapping("/{id}/notes")
    public void addNotes(@RequestBody CreateEventRequest createEventRequest, @PathVariable Long id) {
        replayService.addNote(id, createEventRequest);
    }

    @GetMapping("/{id}/events")
    public List<ReplayEventResponse> getReplayEvents(@PathVariable Long id) {
        return replayService.getEvents(id);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id){
        try {
            replayService.deleteEvent(id);
            return ResponseEntity.ok("Item deleted");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{id}/photos")
    public void addPhotos(@RequestBody MultipartFile file, @PathVariable Long id) {
        replayService.uploadPhoto(id, file);
    }

    @PostMapping(
            value = "/{id}/audio",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Void> uploadVoice(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        replayService.uploadVoice(id, file);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/events/{eventId}")
    public ReplayEvent updateEvent(
            @PathVariable Long eventId,
            @RequestBody UpdateEventRequest request) {

        return replayService.updateEvent(
                eventId,
                request);
    }

    @GetMapping("/{id}")
    public ReplayResponse getReplay(
            @PathVariable Long id) {
        return replayService.getReplay(id);
    }

    @GetMapping("/search")
    public List<SearchResult> search(
            @RequestParam String q) {

        return replayService.search(q);
    }

    @PutMapping("/{replayId}")
    public ResponseEntity<String> editReplay(@PathVariable Long replayId, @RequestBody Replay replay) {
        replayService.updateReplay(replayId, replay);
        return ResponseEntity.ok("replay edited successfully");
    }

    @DeleteMapping("/{replayId}")
    public void deleteReplay(@PathVariable Long replayId) {
        replayService.deleteReplay(replayId);
    }
}
