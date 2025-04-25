package spotify.recommender;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import spotify.recommender.Controller.PlaylistController;
import spotify.recommender.DTO.SpotifyDTO;
import spotify.recommender.Service.CustomOAuth2Service;
import spotify.recommender.Service.SpotifyService;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaylistController.class)
@Import(PlaylistControllerTest.TestConfig.class)
public class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpotifyService spotifyService; // This is now injected via test config

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    public void testCreatePlaylist() throws Exception {
        SpotifyDTO dto = new SpotifyDTO("My Playlist", "A test playlist");
        String json = new ObjectMapper().writeValueAsString(dto);

        // Use the mock
        when(spotifyService.createPlaylist(any(), any(), any()))
                .thenReturn("fakePlaylistId");

        mockMvc.perform(post("/api/playlist/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("fakePlaylistId"));
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public SpotifyService spotifyService() {
            return Mockito.mock(SpotifyService.class);
        }

        @Bean
        public CustomOAuth2Service customOAuth2Service() {
            return Mockito.mock(CustomOAuth2Service.class);
        }
    }
}