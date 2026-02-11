package fr.eletutour.chaosmonkeyapplication.controllers.api;

import fr.eletutour.chaosmonkeyapplication.exception.StreamingException;
import fr.eletutour.chaosmonkeyapplication.services.StreamingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ StreamingController.class })
class StreamingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StreamingService streamingService;

    @Test
    void startStream_ShouldReturnStreamInfo_WhenSuccessful() throws Exception {
        when(streamingService.startStream(1L, 10L))
                .thenReturn(Map.of("status", "READY", "streamUrl", "http://test.com"));

        mockMvc.perform(post("/api/streaming/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": 1, \"videoId\": 10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("READY"))
                .andExpect(jsonPath("$.streamUrl").value("http://test.com"));
    }

    @Test
    void startStream_ShouldReturnProblemDetail_WhenStreamingExceptionThrown() throws Exception {
        when(streamingService.startStream(1L, 10L)).thenThrow(
                new StreamingException(StreamingException.StreamingError.STREAM_INIT_FAILED, "Network error"));

        mockMvc.perform(post("/api/streaming/start")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": 1, \"videoId\": 10}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Streaming Error"))
                .andExpect(jsonPath("$.errorCode").value("STREAM_INIT_FAILED"));
    }
}
