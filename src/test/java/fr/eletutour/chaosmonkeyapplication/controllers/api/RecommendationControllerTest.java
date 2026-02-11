package fr.eletutour.chaosmonkeyapplication.controllers.api;

import fr.eletutour.chaosmonkeyapplication.exception.RecommendationException;
import fr.eletutour.chaosmonkeyapplication.services.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_ShouldReturnList_WhenSuccessful() throws Exception {
        when(recommendationService.getRecommendationsForUser(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/recommendations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void generateRecommendations_ShouldReturnProblemDetail_WhenRecommendationExceptionThrown() throws Exception {
        doThrow(new RecommendationException(RecommendationException.RecommendationError.GENERATION_FAILED,
                "Service crash"))
                .when(recommendationService).generateRecommendations(1L);

        mockMvc.perform(post("/api/recommendations/generate/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Recommendation Error"))
                .andExpect(jsonPath("$.errorCode").value("GENERATION_FAILED"));
    }
}
