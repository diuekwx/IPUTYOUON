package spotify.recommender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;
import spotify.recommender.Service.PlaylistService;
import spotify.recommender.Service.SpotifyService;
import spotify.recommender.Service.UserService;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5173")
@RequestMapping("/api/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final SpotifyService spotifyService;
    private final UserService userService;
    private final String baseUrl = "https://api.spotify.com/v1/";
    @Autowired
    public PlaylistController(PlaylistService playlistService, SpotifyService spotifyService, UserService userService){
        this.playlistService = playlistService;
        this.spotifyService = spotifyService;
        this.userService = userService;
    }



    public static class PlaylistRequest {
        private String name;
        private String description;

        // Getters and setters (or use Lombok)
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
    @PostMapping("/create")
    // Change return type to ResponseEntity<Map<String, String>> or a custom response object
    public ResponseEntity<Map<String, String>> createPlaylist(@RequestBody PlaylistRequest playlistRequest,
                                                              Authentication authentication) { // Inject the Authentication object

        // Get the authenticated principal
        Object principal = authentication.getPrincipal();

        String userId = null;
        if (principal instanceof CustomUserPrincipal) {
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) principal;
            // Ensure getUser() and getSpotify_id() match your CustomUserPrincipal and Users entity
            // Handle potential NullPointerException if userPrincipal.getUser() is null
            if (userPrincipal.getUser() != null) {
                userId = userPrincipal.getUser().getSpotify_id();
            }
        } else if (principal instanceof OAuth2User) {
            // Fallback: get Spotify ID directly from OAuth2User attributes
            // This might be useful for debugging or if CustomUserPrincipal doesn't fully wrap Users entity yet
            userId = ((OAuth2User) principal).getAttribute("id");
        }


        if (userId == null) {
            // This should ideally not happen if authentication was successful,
            // but handle the case where user ID couldn't be retrieved from principal.
            // Return a JSON error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Could not retrieve user ID from authenticated principal.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        System.out.println("Creating playlist for user: " + userId);
        System.out.println("Playlist Name: " + playlistRequest.getName());
        System.out.println("Playlist Description: " + playlistRequest.getDescription());

        // Call your service to create the playlist using the userId and playlistRequest data
        // String createdPlaylistId = playlistService.createSpotifyPlaylist(userId, playlistRequest);
        Users user = userService.getUser(userId).orElse(null);
        String name = playlistRequest.getName();
        String desc = playlistRequest.getDescription();
        String playlistId = spotifyService.createPlaylist(user, name, desc);
        Playlist playlist = new Playlist();
        playlist.setSpotifyPlaylistId(playlistId);
        playlist.setUserOwner(user);
        playlist.setPlaylistName(name);
        playlist.setDescription(desc);
        playlistService.savePlaylist(playlist);

        List<Playlist> userPlaylist = user.getPlaylistList();
        userPlaylist.add(playlist);
//        userPlaylist.clear();
        System.out.println(userPlaylist);
        userService.saveUser(user);

        // Prepare a JSON success response
        Map<String, String> successResponse = new HashMap<>();
        // Replace with the actual created playlist ID if you have one
        successResponse.put("message", "Playlist creation simulated successfully.");
        successResponse.put("userId", userId); // Include user ID in response for confirmation
        // successResponse.put("playlistId", createdPlaylistId); // Include actual playlist ID if available

        // Return the JSON success response
        return ResponseEntity.ok(successResponse);
    }
    @PostMapping("/clear")
    public ResponseEntity<Void> clearPlaylist(Authentication authentication){
        Object principal = authentication.getPrincipal();
        String userId = null;
        if (principal instanceof CustomUserPrincipal) {
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) principal;
            // Ensure getUser() and getSpotify_id() match your CustomUserPrincipal and Users entity
            // Handle potential NullPointerException if userPrincipal.getUser() is null
            if (userPrincipal.getUser() != null) {
                userId = userPrincipal.getUser().getSpotify_id();
            }
        } else if (principal instanceof OAuth2User) {
            // Fallback: get Spotify ID directly from OAuth2User attributes
            // This might be useful for debugging or if CustomUserPrincipal doesn't fully wrap Users entity yet
            userId = ((OAuth2User) principal).getAttribute("id");
        }
        Users user = userService.getUser(userId).orElse(null);

        List<Playlist> userPlaylist = user.getPlaylistList();
        spotifyService.clearPlaylist(user);
        System.out.println("userPlaylist: " + userPlaylist);
        List<Playlist> p = playlistService.getUsersPlaylist(user);
        System.out.println("userPlaylist" + p);
        return ResponseEntity.ok().build();

    }


    @PostMapping("/{playListId}/add-tracks")
    public ResponseEntity<Void> addTracksToPlaylist(
        @PathVariable String playlistId,
        @RequestParam String userId,
        @RequestBody String trackUris){

        Users userid = userService.getUser(userId).orElse(null);
        if (userid == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        spotifyService.addTrackToPlaylist(userid, playlistId, trackUris);
        return  ResponseEntity.ok().build();

    }

    // Return <List<Playlist>> ?
    @GetMapping("/get-user-playlist")
    public ResponseEntity<List<String>> getPlaylist(Authentication authentication){
        String userId = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal) {
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) principal;
            // Ensure getUser() and getSpotify_id() match your CustomUserPrincipal and Users entity
            // Handle potential NullPointerException if userPrincipal.getUser() is null
            if (userPrincipal.getUser() != null) {
                userId = userPrincipal.getUser().getSpotify_id();
            }
        } else if (principal instanceof OAuth2User) {
            // Fallback: get Spotify ID directly from OAuth2User attributes
            // This might be useful for debugging or if CustomUserPrincipal doesn't fully wrap Users entity yet
            userId = ((OAuth2User) principal).getAttribute("id");
        }

        if (userId == null) {
            // This should ideally not happen if authentication was successful,
            // but handle the case where user ID couldn't be retrieved from principal.
            // Return a JSON error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Could not retrieve user ID from authenticated principal.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        Users userid = userService.getUser(userId).orElse(null);
        if (userid == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Map> playlists = spotifyService.getPlaylist(userid);

        List<String> playlistLinks = new ArrayList<>();
        for (Map p: playlists){
            Object bodyOf = p.get("external_urls");

            if (bodyOf instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> externalUrls = (Map<String, Object>) bodyOf;

                String acutalLink = (String) externalUrls.get("spotify");

                playlistLinks.add(acutalLink);
            }
        }
        System.out.println("Playlist links: " + playlistLinks);
        return  ResponseEntity.ok(playlistLinks);

    }


}
