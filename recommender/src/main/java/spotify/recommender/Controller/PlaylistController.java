package spotify.recommender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/playlist")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final SpotifyService spotifyService;
    private final UserService userService;

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

    @PostMapping("/create")
    public ResponseEntity<String> createPlaylist(
            @RequestBody SpotifyDTO dto,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Users user = principal.getUser();
        System.out.print("User: " + user);
        String playlistId = spotifyService.createPlaylist(user, dto.getName(), dto.getDescription());
        return ResponseEntity.ok(playlistId);
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


}
