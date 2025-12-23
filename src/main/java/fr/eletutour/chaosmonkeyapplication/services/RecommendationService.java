package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.models.Recommendation;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import fr.eletutour.chaosmonkeyapplication.repositories.RecommendationRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.WatchHistoryRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);

    private final RecommendationRepository recommendationRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final VideoRepository videoRepository;

    public RecommendationService(RecommendationRepository recommendationRepository,
            WatchHistoryRepository watchHistoryRepository,
            VideoRepository videoRepository) {
        this.recommendationRepository = recommendationRepository;
        this.watchHistoryRepository = watchHistoryRepository;
        this.videoRepository = videoRepository;
    }

    public List<Recommendation> getRecommendationsForUser(Long userId) {
        return recommendationRepository.findTop10ByUserIdOrderByScoreDesc(userId);
    }

    public List<Video> getTrendingVideos() {
        return videoRepository.findTop10ByOrderByViewCountDesc();
    }

    @CircuitBreaker(name = "recommendationServiceCB", fallbackMethod = "generateRecommendationsFallback")
    public void generateRecommendations(Long userId) {
        // Get user's watch history
        List<WatchHistory> history = watchHistoryRepository.findByUserId(userId);

        if (history.isEmpty()) {
            // New user - recommend popular content
            generatePopularRecommendations(userId);
            return;
        }

        // Get genres from watched videos
        Set<String> watchedGenres = new HashSet<>();
        Set<Long> watchedVideoIds = new HashSet<>();

        for (WatchHistory wh : history) {
            watchedVideoIds.add(wh.getVideoId());
            videoRepository.findById(wh.getVideoId()).ifPresent(video -> watchedGenres.add(video.getGenre()));
        }

        // Find videos in same genres
        List<Video> allVideos = videoRepository.findAll();
        List<Video> candidates = allVideos.stream()
                .filter(v -> !watchedVideoIds.contains(v.getId()))
                .filter(v -> watchedGenres.contains(v.getGenre()))
                .collect(Collectors.toList());

        // Create recommendations with scores
        Random random = new Random();
        for (Video video : candidates.subList(0, Math.min(10, candidates.size()))) {
            double score = 0.7 + (random.nextDouble() * 0.3); // 0.7 to 1.0
            String reason = "Because you watched " + watchedGenres.iterator().next() + " content";

            Recommendation rec = new Recommendation(userId, video.getId(), score, reason);
            recommendationRepository.save(rec);
        }
    }

    private void generatePopularRecommendations(Long userId) {
        for (Video video : videoRepository.findTop10ByOrderByViewCountDesc()) {
            Recommendation rec = new Recommendation(
                    userId,
                    video.getId(),
                    0.8,
                    "Trending now");
            recommendationRepository.save(rec);
        }
    }

    // Fallback method for generateRecommendations
    private void generateRecommendationsFallback(Long userId, Throwable t) {
        logger.error("Error generating recommendations for user {}. Falling back to popular recommendations. Error: {}", userId, t.getMessage());
        generatePopularRecommendations(userId);
    }
}
