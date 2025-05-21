package spotify.recommender.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.PlaylistRepo;
import spotify.recommender.Repository.UserRepo;

import java.util.*;


@Service
public class SpotifyService {

    private final UserRepo userRepo;

    private final SpotifyAuthService authService;

    private final PlaylistService playlistService;

    private final PlaylistRepo playlistRepo;

    @Autowired
    public SpotifyService(UserRepo userRepo, SpotifyAuthService authService, PlaylistService playlistService, PlaylistRepo playlistRepo){
        this.userRepo = userRepo;
        this.authService = authService;
        this.playlistService = playlistService;
        this.playlistRepo = playlistRepo;
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


    // should return a list of playlists maybe? how to do ?
    public Object getAllPlaylists(Users user){
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = user.getAccessToken();
         HttpHeaders headers = new HttpHeaders();
         headers.setBearerAuth(accessToken);
         String userId = user.getSpotify_id();
         HttpEntity<Object> request = new HttpEntity<>(headers);
         // //users/ ?
         ResponseEntity<Map> playlists = restTemplate.exchange("https://api.spotify.com/v1/users/" + userId + "/playlists",
                  HttpMethod.GET, request, Map.class);
         return playlists.getBody().get("items");
    }

    @Transactional
    public void clearPlaylist(Users user){
        playlistRepo.deleteByUserOwner(user);
    }

    public List<Map> getPlaylist(Users user){

        List<Playlist> userPlaylist = playlistService.getUsersPlaylist(user);
        System.out.println(userPlaylist);
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = user.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        List<Map> playlistList = new ArrayList<>();
        String playlistId;

        for (Playlist playlist: userPlaylist){
            playlistId = playlist.getSpotifyPlaylistId();

            // token refresh change

            HttpEntity<Object> request = new HttpEntity<>(headers);

            try{
                // exchange for auth header
                ResponseEntity<Map> response = restTemplate.exchange(
                        "https://api.spotify.com/v1/playlists/" + playlistId,
                        HttpMethod.GET,
                        request,
                        Map.class
                );
                if (response.getStatusCode().is2xxSuccessful()){
                    playlistList.add((response.getBody()));
                }
                else {
                    playlistList.add(null);
                }

            }
            catch (HttpClientErrorException.Unauthorized e){

                String refreshed = authService.refreshAccessToken(user);
                user.setAccessToken(refreshed);
                // Retry with new token
                headers = new HttpHeaders();
                headers.setBearerAuth(refreshed);
                request = new HttpEntity<>(headers);

                restTemplate.exchange(
                        "https://api.spotify.com/v1/playlists/" + playlistId,
                        HttpMethod.GET,
                        request,
                        Void.class
                );
            }
        }



        return playlistList;
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
