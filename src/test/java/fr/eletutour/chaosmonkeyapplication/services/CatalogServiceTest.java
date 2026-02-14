package fr.eletutour.chaosmonkeyapplication.services;

import fr.eletutour.chaosmonkeyapplication.exception.CatalogException;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.repositories.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private VideoRepository videoRepository;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        catalogService = new CatalogService(videoRepository);
    }

    @Test
    void incrementViewCount_ShouldIncrementAndSave_WhenVideoExists() {
        // Arrange
        Long videoId = 1L;
        Video video = new Video();
        video.setId(videoId);
        video.setViewCount(10);

        when(videoRepository.findById(videoId)).thenReturn(Optional.of(video));
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Video result = catalogService.incrementViewCount(videoId);

        // Assert
        assertNotNull(result);
        assertEquals(11, result.getViewCount());
        verify(videoRepository).save(video);
    }

    @Test
    void incrementViewCount_ShouldThrowException_WhenVideoNotFound() {
        // Arrange
        Long videoId = 99L;
        when(videoRepository.findById(videoId)).thenReturn(Optional.empty());

        // Act & Assert
        CatalogException exception = assertThrows(CatalogException.class,
                () -> catalogService.incrementViewCount(videoId));

        assertEquals(CatalogException.CatalogError.VIDEO_NOT_FOUND, exception.getError());
    }
}
