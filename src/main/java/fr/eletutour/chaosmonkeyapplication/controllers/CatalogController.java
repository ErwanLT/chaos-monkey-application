package fr.eletutour.chaosmonkeyapplication.controllers;

import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.services.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/videos")
    public List<Video> getAllVideos() {
        return catalogService.getAllVideos();
    }

    @GetMapping("/videos/{id}")
    public ResponseEntity<Video> getVideoById(@PathVariable Long id) {
        return catalogService.getVideoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/videos/genre/{genre}")
    public List<Video> getVideosByGenre(@PathVariable String genre) {
        return catalogService.getVideosByGenre(genre);
    }

    @GetMapping("/videos/type/{type}")
    public List<Video> getVideosByType(@PathVariable Video.VideoType type) {
        return catalogService.getVideosByType(type);
    }

    @GetMapping("/videos/search")
    public List<Video> searchVideos(@RequestParam String query) {
        return catalogService.searchVideos(query);
    }

    @GetMapping("/videos/popular")
    public List<Video> getPopularVideos() {
        return catalogService.getPopularVideos();
    }

    @GetMapping("/videos/top-rated")
    public List<Video> getTopRatedVideos() {
        return catalogService.getTopRatedVideos();
    }
}
