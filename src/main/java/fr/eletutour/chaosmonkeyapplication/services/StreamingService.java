package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.exception.CatalogException;
import fr.eletutour.chaosmonkeyapplication.exception.StreamingException;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import fr.eletutour.chaosmonkeyapplication.repositories.WatchHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StreamingService {

    private final WatchHistoryRepository watchHistoryRepository;
    private final CatalogService catalogService;
    private final UserService userService;

    public StreamingService(WatchHistoryRepository watchHistoryRepository, CatalogService catalogService,
            UserService userService) {
        this.watchHistoryRepository = watchHistoryRepository;
        this.catalogService = catalogService;
        this.userService = userService;
    }

    public Map<String, Object> startStream(Long userId, Long videoId) {
        // Validate user and video
        userService.getUserOrThrow(userId);
        Optional<Video> videoOpt = catalogService.getVideoById(videoId);
        if (videoOpt.isEmpty()) {
            throw new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND, "id=" + videoId);
        }

        try {
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
        } catch (CatalogException e) {
            throw e;
        } catch (Exception e) {
            throw new StreamingException(StreamingException.StreamingError.STREAM_INIT_FAILED, e.getMessage());
        }
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
