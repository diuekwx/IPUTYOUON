package spotify.recommender.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.PlaylistRepo;
import spotify.recommender.Repository.UserRepo;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class SpotifyService {

    private final UserRepo userRepo;

    private final SpotifyAuthService authService;

    private final PlaylistService playlistService;

    private final PlaylistRepo playlistRepo;

    private final UserService userService;

    @Autowired
    public SpotifyService(UserRepo userRepo, SpotifyAuthService authService, PlaylistService playlistService, PlaylistRepo playlistRepo, UserService userService){
        this.userRepo = userRepo;
        this.authService = authService;
        this.playlistService = playlistService;
        this.playlistRepo = playlistRepo;
        this.userService = userService;
    }

    // track Id returns
    //fix returns lol
    public List<String> searchTrack(Users user, String query){
        String accessToken = user.getAccessToken();
        System.out.println("accesstoken" + accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Object> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        String adjusted = query.replace(" ", "+");
        List<String> ids = new ArrayList<>();
        try{
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/search?q=" + adjusted + "&type=track&limit=5",
                    HttpMethod.GET, request, Map.class
            );
            if (response.getStatusCode().is2xxSuccessful()){
                Map<String, Object> map = response.getBody();
                Object tracks = map.get("tracks");

                if (tracks instanceof Map){
                    Map<String, Object> convertedTracks = (Map<String, Object>) tracks;
                    List<Map<String, Object>> items = (List<Map<String, Object>>) convertedTracks.get("items");
                    ids = items.stream().map(item -> (String) item.get("id")).collect(Collectors.toUnmodifiableList());
                }

                return ids;

            }
            else{
                return ids;
            }
        }
        catch (HttpClientErrorException.Unauthorized e){

            String refreshed = authService.refreshAccessToken(user);
            user.setAccessToken(refreshed);
            // Retry with new token
            headers = new HttpHeaders();
            headers.setBearerAuth(refreshed);
            request = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/search?q=" + adjusted + "&type=track&limit=10",
                    HttpMethod.GET, request, Map.class
            );
            return ids;
        }
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

    //LOL for embeds you just need the playlist ID not the entire link HAHAHAHAHAHA whoops...
    public List<String> getPlaylist(Users user){

        List<Playlist> userPlaylist = playlistService.getUsersPlaylist(user);
        System.out.println(userPlaylist);
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = user.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

//        List<Map> playlistList = new ArrayList<>();
        List<String> playlistList = new ArrayList<>();
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
//                    playlistList.add((response.getBody()));
                    playlistList.add(playlistId);
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

        //can porbnbab delete
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

    public Users getUser(Authentication authentication){
        String userId = "";
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal){
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) principal;
            if (userPrincipal.getUser() != null) {
                userId = userPrincipal.getUser().getSpotify_id();
            }
        }
        else if (principal instanceof OAuth2User) {

            userId = ((OAuth2User) principal).getAttribute("id");
        }
        if (userId == null) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Could not retrieve user ID from authenticated principal.");
            return null;
        }

        Users userid = userService.getUser(userId).orElse(null);
        if (userid == null) {
            return null;
        }
        return userid;
    }



    // save playlist ?



}
