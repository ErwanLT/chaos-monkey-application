package fr.eletutour.chaosmonkeyapplication.repositories;

import fr.eletutour.chaosmonkeyapplication.models.WatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    List<WatchHistory> findByUserId(Long userId);

    List<WatchHistory> findByUserIdOrderByWatchedAtDesc(Long userId);

    List<WatchHistory> findByVideoId(Long videoId);

    List<WatchHistory> findByUserIdAndCompleted(Long userId, Boolean completed);
}
