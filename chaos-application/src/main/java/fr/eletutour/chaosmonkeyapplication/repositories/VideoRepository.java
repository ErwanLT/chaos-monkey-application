package fr.eletutour.chaosmonkeyapplication.repositories;

import fr.eletutour.chaosmonkeyapplication.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByGenre(String genre);

    List<Video> findByType(Video.VideoType type);

    List<Video> findByReleaseYear(Integer releaseYear);

    List<Video> findByTitleContainingIgnoreCase(String title);

    List<Video> findTop10ByOrderByViewCountDesc();

    List<Video> findTop10ByOrderByRatingDesc();
}
