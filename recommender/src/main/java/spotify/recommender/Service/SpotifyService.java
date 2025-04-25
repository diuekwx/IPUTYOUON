package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.UserRepo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
public class SpotifyService {

    private final UserRepo userRepo;

    private final SpotifyAuthService authService;

    @Autowired
    public SpotifyService(UserRepo userRepo, SpotifyAuthService authService){
        this.userRepo = userRepo;
        this.authService = authService;
    }

    public void addTrackToPlaylist(Users user, String playlistId, String trackUri){
        String accessToken = user.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> data = new HashMap<>();
        data.put("uris", Collections.singletonList(trackUri));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);

        try{
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity(
                    "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks",
                    request,
                    Void.class
            );
        }
        catch (HttpClientErrorException.Unauthorized e){
            String refreshed = authService.refreshAccessToken(user);
            user.setAccessToken(refreshed);
            addTrackToPlaylist(user, playlistId, trackUri);
        }

    }

    //consider visibility
    public String createPlaylist(Users user, String name, String description){
        String accessToken = user.getAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("description", description);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);

        //delete try catch
        try {
            ResponseEntity<Map> response = new RestTemplate().postForEntity(
                    "https://api.spotify.com/v1/users/" + user.getSpotify_id() + "/playlists",
                    request,
                    Map.class
            );
            return (String) response.getBody().get("id");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                // Token is bad — respond to frontend with 401
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired or invalid");
            }
            throw e; // rethrow other errors
        }

    }



    // save playlist ?



}
