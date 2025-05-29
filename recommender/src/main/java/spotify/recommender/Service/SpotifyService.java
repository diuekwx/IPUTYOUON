package spotify.recommender.Service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.validation.ObjectError;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.DTO.ContributionDTO;
import spotify.recommender.DTO.SpotifyDTO;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.PlaylistRepo;
import spotify.recommender.Repository.TrackSuggestionRepo;
import spotify.recommender.Repository.UserRepo;

import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SpotifyService {

    private final UserRepo userRepo;

    private final SpotifyAuthService authService;

    private final PlaylistService playlistService;

    private final PlaylistRepo playlistRepo;

    private final UserService userService;

    private final TrackSuggestionService trackSuggestionService;

    private final SpotifyTrackService spotifyTrackService;

    private final EncryptionService encryptionService;

    @Autowired
    public SpotifyService(UserRepo userRepo, SpotifyAuthService authService, PlaylistService playlistService, PlaylistRepo playlistRepo, UserService userService,
                          TrackSuggestionService trackSuggestionService, EncryptionService encryptionService, SpotifyTrackService spotifyTrackService){
        this.userRepo = userRepo;
        this.authService = authService;
        this.playlistService = playlistService;
        this.playlistRepo = playlistRepo;
        this.userService = userService;
        this.trackSuggestionService = trackSuggestionService;
        this.encryptionService = encryptionService;
        this.spotifyTrackService = spotifyTrackService;
    }

    public String decryptedAccessToken(Users user) {
        return encryptionService.decryptSafe(user.getAccessToken());
    }


    // track Id returns
    //fix returns lol
    public List<String> searchTrack(Users user, String query){
//        String accessToken = user.getAccessToken();
//        try {
//            String decrypted = encryptionService.decrypt(accessToken);
//        }
//        catch (Exception e){
//            throw new RuntimeException("Bad token");
//        }
        String accessToken = decryptedAccessToken(user);

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

    // owner of the playlist will be adding it, might need to use their refresh token.
    // get rid of recursion probably lol... also check if spotify:track: is prepended for the uris
    public void addTrackToPlaylist(Users user, String playlistId, String trackUri){
        Playlist p = playlistRepo.findBySpotifyPlaylistId(playlistId);
        System.out.println(p);
        Users ownerOfPlaylist = p.getUserOwner();

        System.out.println("BEFOREHAND");
        System.out.println("ownerofplaylist" + ownerOfPlaylist);
        authService.checkExpiry(ownerOfPlaylist);
        String accessToken = decryptedAccessToken(ownerOfPlaylist);
        System.out.println("access token of Owner: " + accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
//        String uri = trackUri.startsWith("spotify:track:") ? trackUri : "spotify:track:" + trackUri;

        //ex {"uris": ["spotify:track:4iV5W9uYEdYUVa79Axb7Rh"]}
        Map<String, Object> data = new HashMap<>();
        data.put("uris", Collections.singletonList(trackUri));
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(data, headers);
        System.out.println(data);
        RestTemplate restTemplate = new RestTemplate();
        try{
            restTemplate.postForEntity(
                    "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks",
                    request,
                    Void.class
            );
            String altered = trackUri.replace("spotify:track:", "");
            SpotifyDTO spotifyDTO = spotifyTrackService.getSongAndArtistName(ownerOfPlaylist, altered);
            trackSuggestionService.saveTrackSuggestion(user, trackUri, p, spotifyDTO.getArtist(), spotifyDTO.getSongName());
            System.out.println("request went through");
        }
        catch (HttpClientErrorException.Unauthorized e){
//        catch (Exception e){
            System.out.println("some error");
            String refreshed = authService.refreshAccessToken(ownerOfPlaylist);
            ownerOfPlaylist.setAccessToken(refreshed);
            userRepo.save(ownerOfPlaylist);
            HttpHeaders newHeaders = new HttpHeaders();
            newHeaders.setBearerAuth(refreshed);
            newHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> retryRequest = new HttpEntity<>(data, newHeaders);
            restTemplate.postForEntity("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks", retryRequest, Void.class);
            String altered = trackUri.replace("spotify:track:", "");
            SpotifyDTO spotifyDTO = spotifyTrackService.getSongAndArtistName(user, trackUri);
            trackSuggestionService.saveTrackSuggestion(user, trackUri, p, spotifyDTO.getArtist(), spotifyDTO.getSongName());

//            addTrackToPlaylist(user, playlistId, trackUri);
        }

    }

    // should return a list of playlists maybe? how to do ?
    public Object getAllPlaylists(Users user){
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = decryptedAccessToken(user);
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

    public List<String> syncDb(Users user){
        // all user playlist in db
        List<Playlist> all = playlistService.getUsersPlaylist(user);

        // all their playlist from spotify and in the db (if removed from spotify library, wont be in here)
        List<String> allPlaylistIds = getAlluserPlaylist(user);

        //go through all playlist in db, remove the ones not on their spotify
        for (Playlist p: all){
            String id = p.getSpotifyPlaylistId();
            if (!allPlaylistIds.contains(id)){
                playlistService.deleteEntry(p);
            }
        }
        return allPlaylistIds;
    }

    public List<String> getAlluserPlaylist(Users user){
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = decryptedAccessToken(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Object> request = new HttpEntity<>(headers);

        List<String> playlists = new ArrayList<>();
            try{
                // exchange for auth header
                String endpoint = "https://api.spotify.com/v1/me/playlists";
                while (endpoint != null) {
                    ResponseEntity<Map> response = restTemplate.exchange(
                            endpoint,
                            HttpMethod.GET,
                            request,
                            Map.class
                    );

                    List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
                    // this is only for getting the playlists weve made through the app -> return this list to the playlist service
                    // go through the list, find all playlist by a user, and then check if any of them arnet in the list, if so, delete
                    for (Map<String, Object> item : items) {
                        Map<String, Object> uri = (Map<String, Object>) item.get("external_urls");
                        String externalUrl = (String) uri.get("spotify");
                        String parsed = externalUrl.replace("https://open.spotify.com/playlist/", "");
                        if (playlistRepo.findBySpotifyPlaylistId(parsed) != null) {
                            playlists.add(parsed);
                        }
                    }
                    // pagination
                    endpoint = (String) response.getBody().get("next");

//                if (response.getStatusCode().is2xxSuccessful()){
////                    playlistList.add((response.getBody()));
//                    playlistList.add(playlistId);
//                }
                }
                return playlists;
            }
            catch (HttpClientErrorException e){
                // error 404 case - should occur when user deletes the playlist from spotify, thus deletes from db
//                if(e.getStatusCode() == HttpStatus.NOT_FOUND){
//                    playlistRepo.delete(playlist);
//                }
                String refreshed = authService.refreshAccessToken(user);
                user.setAccessToken(refreshed);
                // Retry with new token
                headers = new HttpHeaders();
                headers.setBearerAuth(refreshed);
                request = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(
                        "https://api.spotify.com/v1/me/playlists",
                        HttpMethod.GET,
                        request,
                        Map.class
                );

                List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
                // this is only for getting the playlists weve made through the app -> return this list to the playlist service
                // go through the list, find all playlist by a user, and then check if any of them arnet in the list, if so, delete
                for (Map<String, Object> item: items){
                    Map<String, Object> uri = (Map<String, Object>) item.get("external_urls");
                    String externalUrl = (String) uri.get("spotify");
                    String parsed = externalUrl.replace("https://open.spotify.com/playlist/", "");
                    if (playlistRepo.findBySpotifyPlaylistId(parsed) != null){
                        playlists.add(parsed);
                    }
                }
                return playlists;
        }

    }

    //LOL for embeds you just need the playlist ID not the entire link HAHAHAHAHAHA whoops...
    public List<String> getPlaylist(Users user){

        List<Playlist> userPlaylist = playlistService.getUsersPlaylist(user);
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = decryptedAccessToken(user);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

//        List<Map> playlistList = new ArrayList<>();
        List<String> playlistList = new ArrayList<>();
        String playlistId;

        // need to add a check to see if that playlist still exists on their profile
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
            }
            catch (HttpClientErrorException e){
                // error 404 case - should occur when user deletes the playlist from spotify, thus deletes from db
                if(e.getStatusCode() == HttpStatus.NOT_FOUND){
                    playlistRepo.delete(playlist);
                }
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
        String accessToken = decryptedAccessToken(user);

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

        authService.checkExpiry(userid);

        return userid;
    }



    // save playlist ?



}
