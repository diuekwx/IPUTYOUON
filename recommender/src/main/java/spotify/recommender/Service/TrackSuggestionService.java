package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spotify.recommender.DTO.ContributionDTO;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.TrackSuggestion;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.PlaylistRepo;
import spotify.recommender.Repository.TrackSuggestionRepo;
import spotify.recommender.Repository.UserRepo;
import spotify.recommender.Service.SpotifyTrackService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrackSuggestionService {

    private TrackSuggestionRepo trackSuggestionRepo;
    private PlaylistRepo playlistRepo;
    private UserRepo userRepo;
    private SpotifyTrackService spotifyTrackService;



    @Autowired
    public TrackSuggestionService(TrackSuggestionRepo trackSuggestionRepo, PlaylistRepo playlistRepo, UserRepo userRepo, SpotifyTrackService spotifyTrackService){
        this.trackSuggestionRepo = trackSuggestionRepo;
        this.playlistRepo = playlistRepo;
        this.userRepo = userRepo;
        this.spotifyTrackService = spotifyTrackService;

    }

    public List<TrackSuggestion> getByStatus(String status){
        return trackSuggestionRepo.findByStatus(status);
    }

    public List<TrackSuggestion> getByUserId(Long userId){
        return trackSuggestionRepo.findByUserId(userId);
    }

    public List<TrackSuggestion> findByPlaylistIdAndStatus(Long playListId, String status){
        return trackSuggestionRepo.findByPlaylistIdAndStatus(playListId, status);
    }

    public TrackSuggestion saveTrackSuggestion(Users user, String trackUri, Playlist playlisy_id){
        TrackSuggestion trackSuggestion = new TrackSuggestion();
        trackSuggestion.setUser(user);
        trackSuggestion.setTrackUri(trackUri);
        trackSuggestion.setPlaylist(playlisy_id);
        return trackSuggestionRepo.save(trackSuggestion);
    }

    public List<ContributionDTO> getContributors(String playlistId) {
        Playlist p = playlistRepo.findBySpotifyPlaylistId(playlistId);
        Long id = p.getId();
        List<TrackSuggestion> alltrackSuggestions = trackSuggestionRepo.findByPlaylistId(id);

        List<ContributionDTO> contributions = new ArrayList<>();

        for (TrackSuggestion track : alltrackSuggestions) {
            Users user = userRepo.getReferenceById(track.getUser().getId());
            String username = user.getDisplayName();
            String trackUri = track.getTrackUri().replace("spotify:track:", "");

            String artistName = spotifyTrackService.getArtistName(user, trackUri);
            String trackName = spotifyTrackService.getSongName(user, trackUri);

            String contributionText = trackName + " - " + artistName;

            contributions.add(new ContributionDTO(username, contributionText));
        }

        return contributions;
    }




}
