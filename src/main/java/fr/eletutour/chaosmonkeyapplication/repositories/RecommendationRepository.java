package fr.eletutour.chaosmonkeyapplication.repositories;

import fr.eletutour.chaosmonkeyapplication.models.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserId(Long userId);

    List<Recommendation> findByUserIdOrderByScoreDesc(Long userId);

    List<Recommendation> findTop10ByUserIdOrderByScoreDesc(Long userId);
}
