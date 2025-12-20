package fr.eletutour.chaosmonkeyapplication.controllers;

import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import fr.eletutour.chaosmonkeyapplication.services.StreamingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/streaming")
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startStream(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long videoId = request.get("videoId");

        if (userId == null || videoId == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> streamInfo = streamingService.startStream(userId, videoId);
        return ResponseEntity.ok(streamInfo);
    }

    @PostMapping("/progress")
    public WatchHistory updateProgress(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long videoId = ((Number) request.get("videoId")).longValue();
        Integer progress = ((Number) request.get("progress")).intValue();

        return streamingService.updateProgress(userId, videoId, progress);
    }

    @GetMapping("/history/{userId}")
    public List<WatchHistory> getWatchHistory(@PathVariable Long userId) {
        return streamingService.getUserWatchHistory(userId);
    }

    @GetMapping("/completed/{userId}")
    public List<WatchHistory> getCompletedVideos(@PathVariable Long userId) {
        return streamingService.getCompletedVideos(userId);
    }

    @GetMapping("/quality")
    public Map<String, Object> getStreamQuality(@RequestParam String networkSpeed) {
        return streamingService.getStreamQuality(networkSpeed);
    }
}
