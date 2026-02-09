package fr.eletutour.chaosmonkeyapplication.controllers.api;

import fr.eletutour.chaosmonkeyapplication.exception.CatalogException;
import fr.eletutour.chaosmonkeyapplication.models.Video;
import fr.eletutour.chaosmonkeyapplication.services.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ CatalogController.class })
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @Test
    void getVideoById_ShouldReturnVideo_WhenExists() throws Exception {
        Video video = new Video();
        video.setId(1L);
        video.setTitle("Test Video");

        when(catalogService.getVideoById(1L)).thenReturn(Optional.of(video));

        mockMvc.perform(get("/api/catalog/videos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Video"));
    }

    @Test
    void getVideoById_ShouldReturn404_WhenNotFound() throws Exception {
        when(catalogService.getVideoById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/catalog/videos/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVideoById_ShouldReturnProblemDetail_WhenCatalogExceptionThrown() throws Exception {
        when(catalogService.getVideoById(1L))
                .thenThrow(new CatalogException(CatalogException.CatalogError.VIDEO_NOT_FOUND, "id=1"));

        mockMvc.perform(get("/api/catalog/videos/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Catalog Error"))
                .andExpect(jsonPath("$.detail").value("Video not found: id=1"))
                .andExpect(jsonPath("$.errorCode").value("VIDEO_NOT_FOUND"));
    }
}
