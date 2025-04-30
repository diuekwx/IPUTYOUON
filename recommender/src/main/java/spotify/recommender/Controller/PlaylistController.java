package spotify.recommender.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.DTO.SpotifyDTO;
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



//    @PostMapping("/create")
//    public ResponseEntity<Playlist> createPlaylist(
//            @RequestParam String userId,
//            @RequestParam String name,
//            @RequestParam String description){
//        Users user = userService.getUser(userId).orElse(null);
//        if (user == null){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        String playlistId = spotifyService.createPlaylist(user, name, description);
//        Playlist playlist = new Playlist();
//        playlist.setSpotifyPlaylistId(playlistId);
//        playlist.setUserOwner(user);
//        playlist.setPlaylistName(name);
//        playlist.setDescription(description);
//        playlistService.savePlaylist(playlist);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
//    }
    @GetMapping("/current-user")
    public ResponseEntity<String> getCurrentUser(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserPrincipal) {
            CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
            return ResponseEntity.ok(principal.getUser().getSpotify_id());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @GetMapping("/test")
    public ResponseEntity<String> testAuth(HttpServletRequest request) {
        System.out.println("hello");
        return ResponseEntity.ok().build();
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

        // Prepare a JSON success response
        Map<String, String> successResponse = new HashMap<>();
        // Replace with the actual created playlist ID if you have one
        successResponse.put("message", "Playlist creation simulated successfully.");
        successResponse.put("userId", userId); // Include user ID in response for confirmation
        // successResponse.put("playlistId", createdPlaylistId); // Include actual playlist ID if available

        // Return the JSON success response
        return ResponseEntity.ok(successResponse);
    }



//    @PostMapping("/create")
//    public ResponseEntity<String> createPlaylist(
//            @RequestBody SpotifyDTO dto,
//            @RequestParam String userId
//    ) {
//        Optional<Users> userOpt = userService.getUser(userId);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        Users user = userOpt.get();
//        String playlistId = spotifyService.createPlaylist(user, dto.getName(), dto.getDescription());
//        return ResponseEntity.ok(playlistId);
//    }
//    @PostMapping("/create")
//    public ResponseEntity<String> createPlaylist(
//            @RequestBody SpotifyDTO dto
//    ) {
//        Users user = principal.getUser();
//        System.out.print("User: " + user);
//        String playlistId = spotifyService.createPlaylist(user, dto.getName(), dto.getDescription());
//        return ResponseEntity.ok(playlistId);
//    }

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


}
