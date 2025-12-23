package fr.eletutour.chaosmonkeyapplication.controllers.api;

import fr.eletutour.chaosmonkeyapplication.models.Recommendation;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.services.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommandations", description = "Système de recommandations personnalisées et vidéos tendances")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Recommandations personnalisées", description = "Récupère les recommandations personnalisées pour un utilisateur basées sur son historique")
    public List<Recommendation> getRecommendations(
            @Parameter(description = "Identifiant de l'utilisateur") @PathVariable Long userId) {
        return recommendationService.getRecommendationsForUser(userId);
    }

    @PostMapping("/generate/{userId}")
    @Operation(summary = "Générer des recommandations", description = "Génère de nouvelles recommandations pour un utilisateur")
    public ResponseEntity<String> generateRecommendations(
            @Parameter(description = "Identifiant de l'utilisateur") @PathVariable Long userId) {
        recommendationService.generateRecommendations(userId);
        return ResponseEntity.ok("Recommendations generated successfully");
    }

    @GetMapping("/trending")
    @Operation(summary = "Vidéos tendances", description = "Récupère les vidéos actuellement en tendance")
    public List<Video> getTrendingVideos() {
        return recommendationService.getTrendingVideos();
    }
}
