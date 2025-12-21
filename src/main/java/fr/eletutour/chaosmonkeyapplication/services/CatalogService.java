package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogService {

    private final VideoRepository videoRepository;

    public CatalogService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Optional<Video> getVideoById(Long id) {
        return videoRepository.findById(id);
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

    public Video incrementViewCount(Long videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.setViewCount(video.getViewCount() + 1);
            return videoRepository.save(video);
        }
        return null;
    }
}
