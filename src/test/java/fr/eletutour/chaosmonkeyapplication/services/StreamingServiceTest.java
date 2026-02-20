package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.exception.CatalogException;
import fr.eletutour.chaosmonkeyapplication.exception.UserException;
import fr.eletutour.chaosmonkeyapplication.models.User;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.repositories.WatchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StreamingServiceTest {

    @Mock
    private WatchHistoryRepository watchHistoryRepository;
    @Mock
    private CatalogService catalogService;
    @Mock
    private UserService userService;

    private StreamingService streamingService;

    @BeforeEach
    void setUp() {
        streamingService = new StreamingService(watchHistoryRepository, catalogService, userService);
    }

    @Test
    void fallbackStartStream_ShouldReturnUnavailableInfo() {
        // Arrange
        Long userId = 1L;
        Long videoId = 10L;
        String errorMessage = "Service Failure";

        // Act
        Map<String, Object> result = streamingService.fallbackStartStream(userId, videoId, new RuntimeException(errorMessage));

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.get("userId"));
        assertEquals(videoId, result.get("videoId"));
        assertEquals("TEMPORARILY_UNAVAILABLE", result.get("status"));
        assertEquals(errorMessage, result.get("error"));
    }

    @Test
    void startStream_ShouldReturnInfo_WhenUserAndVideoExist() {
        // Arrange
        Long userId = 1L;
        Long videoId = 10L;
        User user = new User();
        Video video = new Video();
        video.setId(videoId);

        when(userService.getUserOrThrow(userId)).thenReturn(user);
        when(catalogService.getVideoById(videoId)).thenReturn(Optional.of(video));

        // Act
        Map<String, Object> result = streamingService.startStream(userId, videoId);

        // Assert
        assertNotNull(result);
        assertEquals("READY", result.get("status"));
        assertEquals(videoId, result.get("videoId"));
        verify(catalogService).incrementViewCount(videoId);
    }

    @Test
    void startStream_ShouldThrowUserException_WhenUserNotFound() {
        // Arrange
        Long userId = 99L;
        Long videoId = 10L;

        when(userService.getUserOrThrow(userId)).thenThrow(new UserException(UserException.UserError.USER_NOT_FOUND));

        // Act & Assert
        UserException exception = assertThrows(UserException.class,
                () -> streamingService.startStream(userId, videoId));
        assertEquals(UserException.UserError.USER_NOT_FOUND, exception.getError());
    }

    @Test
    void startStream_ShouldThrowCatalogException_WhenVideoNotFound() {
        // Arrange
        Long userId = 1L;
        Long videoId = 99L;

        when(userService.getUserOrThrow(userId)).thenReturn(new User());
        when(catalogService.getVideoById(videoId)).thenReturn(Optional.empty());

        // Act & Assert
        CatalogException exception = assertThrows(CatalogException.class,
                () -> streamingService.startStream(userId, videoId));
        assertEquals(CatalogException.CatalogError.VIDEO_NOT_FOUND, exception.getError());
    }
}
