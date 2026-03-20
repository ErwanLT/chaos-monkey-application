package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.exception.CatalogException;
import fr.eletutour.chaosmonkeyapplication.exception.StreamingException;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import fr.eletutour.chaosmonkeyapplication.repositories.WatchHistoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StreamingService {

    private static final Logger log = LoggerFactory.getLogger(StreamingService.class);

    private final WatchHistoryRepository watchHistoryRepository;
    private final CatalogService catalogService;
    private final UserService userService;

    public StreamingService(WatchHistoryRepository watchHistoryRepository, CatalogService catalogService,
            UserService userService) {
        this.watchHistoryRepository = watchHistoryRepository;
        this.catalogService = catalogService;
        this.userService = userService;
    }

    /**
     * Retourne une ressource pour le streaming vidéo.
     */
    public Resource getVideoResource(Long id) {
        Video video = catalogService.getVideoById(id).orElseThrow(() -> new CatalogException(
                CatalogException.CatalogError.VIDEO_NOT_FOUND, "id=" + id));
        String trailerUrl = video.getTrailerUrl();
        if (trailerUrl == null || trailerUrl.isEmpty()) {
            throw new CatalogException(
                    CatalogException.CatalogError.VIDEO_NOT_FOUND,
                    "Aucun contenu disponible pour id=" + id);
        }
        if (trailerUrl.startsWith("/")) {
            trailerUrl = trailerUrl.substring(1);
        }
        log.info("[STREAM-SERVICE] Préparation de la ressource : {}", trailerUrl);
        Resource resource = new ClassPathResource("static/" + trailerUrl);
        if (!resource.exists() || !resource.isReadable()) {
            throw new StreamingException(
                    StreamingException.StreamingError.PLAYBACK_ERROR,
                    "Ressource introuvable ou illisible: " + trailerUrl);
        }
        return resource;
    }

    @Retry(name = "streamingService", fallbackMethod = "fallbackStartStream")
    @CircuitBreaker(name = "streamingService")
    public Map<String, Object> startStream(Long userId, Long videoId) {
        log.info("Attempting to start stream for user: {} and video: {}", userId, videoId);
        // Validate user and video
        userService.getUserOrThrow(userId);
        Optional<Video> videoOpt = catalogService.getVideoById(videoId);
        if (videoOpt.isEmpty()) {
            throw new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND, "id=" + videoId);
        }

        try {
            // Simulate stream initialization with a slight delay to mimic network
            Thread.sleep(100); 

            Map<String, Object> streamInfo = new HashMap<>();
            streamInfo.put("userId", userId);
            streamInfo.put("videoId", videoId);
            // Point to a local reactive streaming endpoint we will create
            streamInfo.put("streamUrl", "/api/streaming/video/" + videoId);
            streamInfo.put("quality", "HD");
            streamInfo.put("status", "READY");

            // Increment view count
            catalogService.incrementViewCount(videoId);

            return streamInfo;
        } catch (CatalogException e) {
            throw e;
        } catch (Exception e) {
            throw new StreamingException(StreamingException.StreamingError.STREAM_INIT_FAILED, e.getMessage());
        }
    }

    public Map<String, Object> fallbackStartStream(Long userId, Long videoId, Throwable t) {
        log.error("Streaming service error for user {} and video {}. Reason: {}", userId, videoId, t.getMessage());
        log.info("Returning unavailable status as fallback for stream request");
        Map<String, Object> streamInfo = new HashMap<>();
        streamInfo.put("userId", userId);
        streamInfo.put("videoId", videoId);
        streamInfo.put("streamUrl", "");
        streamInfo.put("quality", "N/A");
        streamInfo.put("status", "TEMPORARILY_UNAVAILABLE");
        streamInfo.put("error", t.getMessage());
        return streamInfo;
    }

    public WatchHistory updateProgress(Long userId, Long videoId, Integer progressPercentage) {
        WatchHistory history = new WatchHistory(userId, videoId, progressPercentage);
        return watchHistoryRepository.save(history);
    }

    public List<WatchHistory> getUserWatchHistory(Long userId) {
        return watchHistoryRepository.findByUserIdOrderByWatchedAtDesc(userId);
    }

    public List<WatchHistory> getCompletedVideos(Long userId) {
        return watchHistoryRepository.findByUserIdAndCompleted(userId, true);
    }

    public Map<String, Object> getStreamQuality(String networkSpeed) {
        Map<String, Object> qualityInfo = new HashMap<>();

        // Simulate quality adaptation based on network
        switch (networkSpeed.toLowerCase()) {
            case "fast":
                qualityInfo.put("quality", "4K");
                qualityInfo.put("bitrate", "25 Mbps");
                break;
            case "medium":
                qualityInfo.put("quality", "HD");
                qualityInfo.put("bitrate", "5 Mbps");
                break;
            default:
                qualityInfo.put("quality", "SD");
                qualityInfo.put("bitrate", "1 Mbps");
        }

        return qualityInfo;
    }
}
