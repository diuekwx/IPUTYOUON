package spotify.recommender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import spotify.recommender.DTO.ContributionDTO;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;
import spotify.recommender.Service.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5173")
@RequestMapping("/api/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final SpotifyService spotifyService;
    private final UserService userService;
    private final TrackSuggestionService trackSuggestionService;
    private final String baseUrl = "https://api.spotify.com/v1/";

    @Autowired
    public PlaylistController(PlaylistService playlistService, SpotifyService spotifyService, UserService userService, TrackSuggestionService trackSuggestionService){
        this.playlistService = playlistService;
        this.spotifyService = spotifyService;
        this.userService = userService;
        this.trackSuggestionService = trackSuggestionService;
    }


    // lowkey should just make a method to get the auth principal  and userID auth check


    public static class PlaylistRequest {
        private String name;
        private String description;

        // Getters and setters (or use Lombok)
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class TrackSuggestionRequest {
        private String query;

        public String getQuery(){return query;};
        public void setQuery(String query){this.query = query; }
    }

    @PostMapping("/create")
    // Change return type to ResponseEntity<Map<String, String>> or a custom response object
    public ResponseEntity<Map<String, String>> createPlaylist(@RequestBody PlaylistRequest playlistRequest,
                                                              Authentication authentication) { // Inject the Authentication object

        Users userid = spotifyService.getUser(authentication);

        System.out.println("Creating playlist for user: " + userid);
        System.out.println("Playlist Name: " + playlistRequest.getName());
        System.out.println("Playlist Description: " + playlistRequest.getDescription());

        // String createdPlaylistId = playlistService.createSpotifyPlaylist(userId, playlistRequest);

        String name = playlistRequest.getName();
        String desc = playlistRequest.getDescription();
        String playlistId = spotifyService.createPlaylist(userid, name, desc);

        Playlist playlist = playlistService.savePlaylist(userid, name, desc, playlistId);

        List<Playlist> userPlaylist = userid.getPlaylistList();
        userPlaylist.add(playlist);
//        userPlaylist.clear();
        System.out.println(userPlaylist);
        userService.saveUser(userid);

        // prepare a JSON success response
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Playlist creation simulated successfully.");
        successResponse.put("userId", userid.getSpotify_id()); // Include user ID in response for confirmation


        // Return the JSON success response
        return ResponseEntity.ok(successResponse);
    }
    @PostMapping("/clear")
    public ResponseEntity<Void> clearPlaylist(Authentication authentication){
        Users userid = spotifyService.getUser(authentication);

        List<Playlist> userPlaylist = userid.getPlaylistList();
        spotifyService.clearPlaylist(userid);
        System.out.println("userPlaylist: " + userPlaylist);
        List<Playlist> p = playlistService.getUsersPlaylist(userid);
        System.out.println("userPlaylist" + p);
        return ResponseEntity.ok().build();

    }

    //should return some track ID
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchQuery(Authentication authentication, @RequestParam String query){
        Users userid  = spotifyService.getUser(authentication);
        List<String> getTracks = spotifyService.searchTrack(userid, query);
        return ResponseEntity.ok(getTracks);
    }

    //uhhh authentication necessary? since were using the other persons token anyway to add...
    @PostMapping("/{playListId}/add-tracks")
    public ResponseEntity<Boolean> addTracksToPlaylist(
            Authentication authentication,
        @PathVariable String playListId,
        @RequestBody String uris){

        Users userid = spotifyService.getUser(authentication);
        if (userid == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        System.out.println(userid);
        boolean added = spotifyService.addTrackToPlaylist(userid, playListId, uris);
        return  ResponseEntity.ok(added);
    }

    // Return <List<Playlist>> ?
    @GetMapping("/get-user-playlist")
    public ResponseEntity<List<String>> getPlaylist(Authentication authentication){
        Users userid = spotifyService.getUser(authentication);
        List<String> playlists = spotifyService.syncDb(userid);

//        List<String> playlistLinks = new ArrayList<>();
//        for (Map p: playlists){
//            Object bodyOf = p.get("external_urls");
//
//            if (bodyOf instanceof Map) {
//                @SuppressWarnings("unchecked")
//                Map<String, Object> externalUrls = (Map<String, Object>) bodyOf;
//
//                String acutalLink = (String) externalUrls.get("spotify");
//
//                playlistLinks.add(acutalLink);
//            }
//        }
//        System.out.println("Playlist links: " + playlistLinks);
//        return  ResponseEntity.ok(playlistLinks);
        System.out.println("Playlist links: " + playlists);
        return  ResponseEntity.ok(playlists);

    }

    @GetMapping("/feed")
    public ResponseEntity<List<String>> displayFeed(Authentication authentication){

        Users userid = spotifyService.getUser(authentication);
        List<String> feedPlaylist = playlistService.getUserFeed(userid, 2);
        while (feedPlaylist.size() != 2){
            feedPlaylist = playlistService.getUserFeed(userid, 2);
        }
        System.out.println(feedPlaylist);
        return ResponseEntity.ok(feedPlaylist);

    }

    @GetMapping("/{playListId}/contributors")
    public ResponseEntity< List<ContributionDTO>> displayContributors(Authentication authentication, @PathVariable String playListId){
        Users userid = spotifyService.getUser(authentication);
        List<ContributionDTO> contributors = trackSuggestionService.getContributors(playListId);
        return ResponseEntity.ok(contributors);

    }

//    @PostMapping("/addTrack")
//    public ResponseEntity<String> addTrack(Authentication authentication){
//
//    }


}
