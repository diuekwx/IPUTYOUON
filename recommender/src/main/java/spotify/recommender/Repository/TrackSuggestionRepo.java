package spotify.recommender.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.TrackSuggestion;

import java.util.List;

@Repository
public interface TrackSuggestionRepo extends JpaRepository<TrackSuggestion, Long> {


    List<TrackSuggestion> findByStatus(String status);
    List<TrackSuggestion> findByUserId(Long userId);
    List<TrackSuggestion> findByPlaylistIdAndStatus(Long playlistId, String status);
    List<TrackSuggestion> findByPlaylistId(Long playlistId);

}
