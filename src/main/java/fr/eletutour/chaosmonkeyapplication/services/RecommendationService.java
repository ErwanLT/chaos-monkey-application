package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.exception.RecommendationException;
import fr.eletutour.chaosmonkeyapplication.exception.UserException;
import fr.eletutour.chaosmonkeyapplication.models.Recommendation;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import fr.eletutour.chaosmonkeyapplication.repositories.RecommendationRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.WatchHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final WatchHistoryRepository watchHistoryRepository;
    private final VideoRepository videoRepository;
    private final UserService userService;

    public RecommendationService(RecommendationRepository recommendationRepository,
            WatchHistoryRepository watchHistoryRepository,
            VideoRepository videoRepository,
            UserService userService) {
        this.recommendationRepository = recommendationRepository;
        this.watchHistoryRepository = watchHistoryRepository;
        this.videoRepository = videoRepository;
        this.userService = userService;
    }

    public List<Recommendation> getRecommendationsForUser(Long userId) {
        return recommendationRepository.findTop10ByUserIdOrderByScoreDesc(userId);
    }

    public List<Video> getTrendingVideos() {
        return videoRepository.findTop10ByOrderByViewCountDesc();
    }

    public void generateRecommendations(Long userId) {
        try {
            // Validate user
            userService.getUserOrThrow(userId);

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
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            throw new RecommendationException(RecommendationException.RecommendationError.GENERATION_FAILED,
                    e.getMessage());
        }
    }

    private void generatePopularRecommendations(Long userId) {
        List<Video> popular = videoRepository.findTop10ByOrderByViewCountDesc();

        for (Video video : popular) {
            Recommendation rec = new Recommendation(
                    userId,
                    video.getId(),
                    0.8,
                    "Trending now");
            recommendationRepository.save(rec);
        }
    }
}
