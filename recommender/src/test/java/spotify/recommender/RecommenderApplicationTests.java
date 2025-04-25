package spotify.recommender;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import spotify.recommender.Controller.PlaylistController;
import spotify.recommender.DTO.SpotifyDTO;
import spotify.recommender.Service.CustomOAuth2Service;
import spotify.recommender.Service.SpotifyService;

@SpringBootTest
class RecommenderApplicationTests {

	@Test
	void contextLoads() {
	}




}
