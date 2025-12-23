package fr.eletutour.chaosmonkeyapplication.controllers.api;

import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.services.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@Tag(name = "Catalogue", description = "Gestion du catalogue de vidéos")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/videos")
    @Operation(summary = "Liste toutes les vidéos", description = "Récupère la liste complète de toutes les vidéos disponibles dans le catalogue")
    public List<Video> getAllVideos() {
        return catalogService.getAllVideos();
    }

    @GetMapping("/videos/{id}")
    @Operation(summary = "Détails d'une vidéo", description = "Récupère les détails d'une vidéo spécifique par son identifiant")
    public ResponseEntity<Video> getVideoById(
            @Parameter(description = "Identifiant de la vidéo") @PathVariable Long id) {
        return catalogService.getVideoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/videos/genre/{genre}")
    @Operation(summary = "Vidéos par genre", description = "Récupère toutes les vidéos d'un genre spécifique")
    public List<Video> getVideosByGenre(
            @Parameter(description = "Genre de la vidéo (Action, Drama, Comedy, etc.)") @PathVariable String genre) {
        return catalogService.getVideosByGenre(genre);
    }

    @GetMapping("/videos/type/{type}")
    @Operation(summary = "Vidéos par type", description = "Récupère toutes les vidéos d'un type spécifique (MOVIE, SERIES, DOCUMENTARY)")
    public List<Video> getVideosByType(
            @Parameter(description = "Type de vidéo") @PathVariable Video.VideoType type) {
        return catalogService.getVideosByType(type);
    }

    @GetMapping("/videos/search")
    @Operation(summary = "Recherche de vidéos", description = "Recherche des vidéos par titre ou description")
    public List<Video> searchVideos(
            @Parameter(description = "Terme de recherche") @RequestParam String query) {
        return catalogService.searchVideos(query);
    }

    @GetMapping("/videos/popular")
    @Operation(summary = "Vidéos populaires", description = "Récupère les vidéos les plus populaires (nombre de vues > 1000)")
    public List<Video> getPopularVideos() {
        return catalogService.getPopularVideos();
    }

    @GetMapping("/videos/top-rated")
    @Operation(summary = "Vidéos les mieux notées", description = "Récupère les vidéos avec les meilleures notes (rating >= 4.5)")
    public List<Video> getTopRatedVideos() {
        return catalogService.getTopRatedVideos();
    }
}
