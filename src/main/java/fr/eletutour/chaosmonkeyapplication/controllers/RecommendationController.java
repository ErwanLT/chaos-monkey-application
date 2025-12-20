package fr.eletutour.chaosmonkeyapplication.controllers;

import fr.eletutour.chaosmonkeyapplication.models.Recommendation;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.services.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public List<Recommendation> getRecommendations(@PathVariable Long userId) {
        return recommendationService.getRecommendationsForUser(userId);
    }

    @PostMapping("/generate/{userId}")
    public ResponseEntity<String> generateRecommendations(@PathVariable Long userId) {
        recommendationService.generateRecommendations(userId);
        return ResponseEntity.ok("Recommendations generated successfully");
    }

    @GetMapping("/trending")
    public List<Video> getTrendingVideos() {
        return recommendationService.getTrendingVideos();
    }
}
