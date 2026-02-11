package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.exception.CatalogException;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    private final VideoRepository videoRepository;

    public CatalogService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @CircuitBreaker(name = "catalogServiceCB", fallbackMethod = "getAllVideosFallback")
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    private List<Video> getAllVideosFallback(Throwable t) {
        logger.error("Error retrieving all videos. Returning empty list. Error: {}", t.getMessage());
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "catalogServiceCB", fallbackMethod = "getVideoByIdFallback")
    public Optional<Video> getVideoById(Long id) {
        return videoRepository.findById(id);
    }

    private Optional<Video> getVideoByIdFallback(Long id, Throwable t) {
        logger.error("Error retrieving video by ID {}. Returning empty optional. Error: {}", id, t.getMessage());
        return Optional.empty();
    }

    public List<Video> getVideosByGenre(String genre) {
        return videoRepository.findByGenre(genre);
    }

    public List<Video> getVideosByType(Video.VideoType type) {
        return videoRepository.findByType(type);
    }

    public List<Video> searchVideos(String query) {
        if (query.isBlank()) {
            return getAllVideos();
        }
        return videoRepository.findByTitleContainingIgnoreCase(query);
    }

    public List<Video> getPopularVideos() {
        return videoRepository.findTop10ByOrderByViewCountDesc();
    }

    public List<Video> getTopRatedVideos() {
        return videoRepository.findTop10ByOrderByRatingDesc();
    }

    @Retry(name = "catalogServiceRetry")
    public Video incrementViewCount(Long videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.setViewCount(video.getViewCount() + 1);
            return videoRepository.save(video);
        }
        throw new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND, "id=" + videoId);
    }
}
