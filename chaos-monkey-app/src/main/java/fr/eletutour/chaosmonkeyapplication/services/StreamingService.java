package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import fr.eletutour.chaosmonkeyapplication.repositories.WatchHistoryRepository;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StreamingService {

    private final WatchHistoryRepository watchHistoryRepository;
    private final CatalogService catalogService;

    public StreamingService(WatchHistoryRepository watchHistoryRepository, CatalogService catalogService) {
        this.watchHistoryRepository = watchHistoryRepository;
        this.catalogService = catalogService;
    }

    @Retry(name = "streamingServiceRetry")
    public Map<String, Object> startStream(Long userId, Long videoId) {
        // Simulate stream initialization
        Map<String, Object> streamInfo = new HashMap<>();
        streamInfo.put("userId", userId);
        streamInfo.put("videoId", videoId);
        streamInfo.put("streamUrl", "https://cdn.streaming.example.com/stream/" + videoId);
        streamInfo.put("quality", "HD");
        streamInfo.put("status", "READY");

        // Increment view count
        catalogService.incrementViewCount(videoId);

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
