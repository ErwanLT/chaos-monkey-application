package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.models.User;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.repositories.RecommendationRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "app.dataloader.enabled=false")
class ResilienceIntegrationTest {

    @Autowired
    private StreamingService streamingService;

    @Autowired
    private RecommendationService recommendationService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CatalogService catalogService;

    @MockitoBean
    private RecommendationRepository recommendationRepository;

    @MockitoBean
    private VideoRepository videoRepository;

    @Test
    void streamingService_startStream_ShouldRetryAndThenFallback() {
        // Arrange
        Long userId = 1L;
        Long videoId = 10L;
        
        // Mock userService to throw exception to trigger retry
        when(userService.getUserOrThrow(anyLong())).thenThrow(new RuntimeException("Simulated Failure"));

        // Act
        Map<String, Object> result = streamingService.startStream(userId, videoId);

        // Assert
        assertNotNull(result);
        assertEquals("TEMPORARILY_UNAVAILABLE", result.get("status"));
        assertEquals("Simulated Failure", result.get("error"));
        
        // Verify that it was called multiple times (3 attempts as configured in application.properties)
        verify(userService, times(3)).getUserOrThrow(userId);
    }

    @Test
    void recommendationService_getRecommendations_ShouldFallbackOnFailure() {
        // Arrange
        Long userId = 1L;
        when(recommendationRepository.findTop10ByUserIdOrderByScoreDesc(anyLong()))
                .thenThrow(new RuntimeException("Database Error"));
        
        Video popularVideo = new Video();
        popularVideo.setId(100L);
        popularVideo.setTitle("Popular Movie");
        when(videoRepository.findTop10ByOrderByViewCountDesc()).thenReturn(List.of(popularVideo));

        // Act
        var recommendations = recommendationService.getRecommendationsForUser(userId);

        // Assert
        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertEquals(100L, recommendations.getFirst().getVideoId());
        assertEquals("Popular now (Fallback)", recommendations.getFirst().getReason());
        
        verify(recommendationRepository, atLeastOnce()).findTop10ByUserIdOrderByScoreDesc(userId);
    }

    @Test
    void recommendationService_generateRecommendations_ShouldRetryAndThenFallback() {
        // Arrange
        Long userId = 1L;
        // Mock userService to throw exception to trigger retry
        when(userService.getUserOrThrow(anyLong())).thenThrow(new RuntimeException("Simulated Failure"));
        
        Video popularVideo = new Video();
        popularVideo.setId(100L);
        when(videoRepository.findTop10ByOrderByViewCountDesc()).thenReturn(List.of(popularVideo));

        // Act
        recommendationService.generateRecommendations(userId);

        // Assert
        // Verify that it was called 3 times as configured
        verify(userService, times(3)).getUserOrThrow(userId);
        // Verify that the fallback was called (it calls generatePopularRecommendations)
        verify(recommendationRepository, atLeastOnce()).save(any());
    }
}
