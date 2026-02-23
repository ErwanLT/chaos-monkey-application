package fr.eletutour.chaosmonkeyapplication.controllers.api;

import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import fr.eletutour.chaosmonkeyapplication.services.StreamingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/streaming")
@Tag(name = "Streaming", description = "Gestion du streaming vidéo et de l'historique de visionnage")
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @PostMapping("/start")
    @Operation(summary = "Démarrer un streaming", description = "Démarre une session de streaming pour un utilisateur et une vidéo")
    public ResponseEntity<Map<String, Object>> startStream(
            @Parameter(description = "Requête contenant userId et videoId") @RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long videoId = request.get("videoId");

        if (userId == null || videoId == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<String, Object> streamInfo = streamingService.startStream(userId, videoId);
        return ResponseEntity.ok(streamInfo);
    }

    @PostMapping("/progress")
    @Operation(summary = "Mettre à jour la progression", description = "Met à jour la progression de visionnage d'une vidéo pour un utilisateur")
    public WatchHistory updateProgress(
            @Parameter(description = "Requête contenant userId, videoId et progress") @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long videoId = ((Number) request.get("videoId")).longValue();
        Integer progress = ((Number) request.get("progress")).intValue();

        return streamingService.updateProgress(userId, videoId, progress);
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Historique de visionnage", description = "Récupère l'historique complet de visionnage d'un utilisateur")
    public List<WatchHistory> getWatchHistory(
            @Parameter(description = "Identifiant de l'utilisateur") @PathVariable Long userId) {
        return streamingService.getUserWatchHistory(userId);
    }

    @GetMapping("/completed/{userId}")
    @Operation(summary = "Vidéos complétées", description = "Récupère la liste des vidéos complétées par un utilisateur (progression >= 90%)")
    public List<WatchHistory> getCompletedVideos(
            @Parameter(description = "Identifiant de l'utilisateur") @PathVariable Long userId) {
        return streamingService.getCompletedVideos(userId);
    }

    @GetMapping("/quality")
    @Operation(summary = "Qualité de streaming", description = "Détermine la qualité de streaming optimale en fonction de la vitesse réseau")
    public Map<String, Object> getStreamQuality(
            @Parameter(description = "Vitesse réseau (slow, medium, fast)") @RequestParam String networkSpeed) {
        return streamingService.getStreamQuality(networkSpeed);
    }
}
