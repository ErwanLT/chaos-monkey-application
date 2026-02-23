package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.exception.RecommendationException;
import fr.eletutour.chaosmonkeyapplication.exception.UserException;
import fr.eletutour.chaosmonkeyapplication.models.Recommendation;
import fr.eletutour.chaosmonkeyapplication.models.User;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.repositories.RecommendationRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import fr.eletutour.chaosmonkeyapplication.repositories.WatchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private WatchHistoryRepository watchHistoryRepository;
    @Mock
    private VideoRepository videoRepository;
    @Mock
    private UserService userService;

    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(recommendationRepository, watchHistoryRepository,
                videoRepository, userService);
    }

    @Test
    void fallbackRecommendations_ShouldReturnPopularContent() {
        // Arrange
        Long userId = 1L;
        Video popularVideo = new Video();
        popularVideo.setId(10L);
        popularVideo.setGenre("Action");
        when(videoRepository.findTop10ByOrderByViewCountDesc()).thenReturn(List.of(popularVideo));

        // Act
        List<Recommendation> result = recommendationService.fallbackRecommendations(userId, new RuntimeException("DB Down"));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().getVideoId());
        assertTrue(result.getFirst().getReason().contains("Fallback"));
    }

    @Test
    void fallbackGenerateRecommendations_ShouldGeneratePopularContent() {
        // Arrange
        Long userId = 1L;
        Video popularVideo = new Video();
        popularVideo.setId(10L);
        when(videoRepository.findTop10ByOrderByViewCountDesc()).thenReturn(List.of(popularVideo));

        // Act
        recommendationService.fallbackGenerateRecommendations(userId, new RuntimeException("Retry failed"));

        // Assert
        verify(recommendationRepository, atLeastOnce()).save(any(Recommendation.class));
    }

    @Test
    void generateRecommendations_ShouldVerifyUserExistence() {
        // Arrange
        Long userId = 1L;
        when(userService.getUserOrThrow(userId)).thenReturn(new User());
        when(watchHistoryRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(videoRepository.findTop10ByOrderByViewCountDesc()).thenReturn(Collections.emptyList());

        // Act
        recommendationService.generateRecommendations(userId);

        // Assert
        verify(userService).getUserOrThrow(userId);
    }

    @Test
    void generateRecommendations_ShouldThrowUserException_WhenUserNotFound() {
        // Arrange
        Long userId = 99L;
        when(userService.getUserOrThrow(userId)).thenThrow(new UserException(UserException.UserError.USER_NOT_FOUND));

        // Act & Assert
        UserException exception = assertThrows(UserException.class,
                () -> recommendationService.generateRecommendations(userId));
        assertEquals(UserException.UserError.USER_NOT_FOUND, exception.getError());
    }

    @Test
    void generateRecommendations_ShouldThrowRecommendationException_OnGenericError() {
        // Arrange
        Long userId = 1L;
        when(userService.getUserOrThrow(userId)).thenThrow(new RuntimeException("Unexpected DB error"));

        // Act & Assert
        RecommendationException exception = assertThrows(RecommendationException.class,
                () -> recommendationService.generateRecommendations(userId));
        assertEquals(RecommendationException.RecommendationError.GENERATION_FAILED, exception.getError());
    }
}
