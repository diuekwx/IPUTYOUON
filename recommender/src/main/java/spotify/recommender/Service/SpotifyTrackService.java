package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import spotify.recommender.DTO.ContributionDTO;
import spotify.recommender.DTO.SpotifyDTO;
import spotify.recommender.Entities.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpotifyTrackService {

    private SpotifyAuthService authService;
    private EncryptionService encryptionService;

    @Autowired
    public SpotifyTrackService(SpotifyAuthService authService, EncryptionService encryptionService){
        this.authService = authService;
        this.encryptionService = encryptionService;
    }

    public SpotifyDTO getSongAndArtistName(Users user, String trackUri){
        String accessToken = user.getAccessToken();
        String decrypted = encryptionService.decryptSafe(accessToken);
        System.out.println("decrypted accesss token" + decrypted);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(decrypted);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try{
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/tracks/" + trackUri,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            List<Map<String, Object>> artists = (List<Map<String, Object>>) response.getBody().get("artists");
            List<String> artistNames = new ArrayList<>();
            for (Map<String, Object> artist : artists) {
                artistNames.add((String) artist.get("name"));
            }
            String joinedArtistNames = String.join(", ", artistNames);

            String songName = (String) response.getBody().get("name");
            System.out.println("success");
            return new SpotifyDTO(joinedArtistNames, songName);
        }
        catch(Exception e){
            System.out.println("failure");
            throw new RuntimeException(e);
        }

    }

    public String getArtistName(Users user, String trackUri){
        String accessToken = user.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/tracks/" + trackUri,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            List<Map<String, Object>> artists = (List<Map<String, Object>>) response.getBody().get("artists");

            List<String> artistNames = new ArrayList<>();
            for (Map<String, Object> artist : artists) {
                artistNames.add((String) artist.get("name"));
            }

            String joinedArtistNames = String.join(", ", artistNames);
            System.out.println("Artists: " + joinedArtistNames);
            return  joinedArtistNames;
        }
        catch (HttpClientErrorException.Unauthorized e) {
            String refreshed = authService.refreshAccessToken(user);
            user.setAccessToken(refreshed);
            // Retry with new token
            headers = new HttpHeaders();
            headers.setBearerAuth(refreshed);
            request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/tracks/" + trackUri,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            Object res = response.getBody();
            List<Map<String, Object>> artists = (List<Map<String, Object>>) response.getBody().get("artists");

            List<String> artistNames = new ArrayList<>();
            for (Map<String, Object> artist : artists) {
                artistNames.add((String) artist.get("name"));
            }

            String joinedArtistNames = String.join(", ", artistNames);
            System.out.println("Artists: " + joinedArtistNames);
            return  joinedArtistNames;
        }

    }

    public String getSongName(Users user, String trackUri){
        String accessToken = user.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/tracks/" + trackUri,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            Object res = response.getBody();
            if (res instanceof Map){
                Map<String, Object> q = (Map<String, Object>) res;
                String songName = (String) q.get("name");
                return songName;
            }
        }
        catch (HttpClientErrorException.Unauthorized e) {
            String refreshed = authService.refreshAccessToken(user);
            user.setAccessToken(refreshed);
            // Retry with new token
            headers = new HttpHeaders();
            headers.setBearerAuth(refreshed);
            request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/tracks/" + trackUri,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            Object res = response.getBody();
            if (res instanceof Map){
                Map<String, Object> q = (Map<String, Object>) res;
                String songName = (String) q.get("name");
                return songName;
            }
        }
        return null;
    }
}
