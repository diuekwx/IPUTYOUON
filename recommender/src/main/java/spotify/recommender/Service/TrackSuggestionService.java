package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spotify.recommender.Entities.TrackSuggestion;
import spotify.recommender.Repository.TrackSuggestionRepo;

import java.util.List;

@Service
public class TrackSuggestionService {

    private TrackSuggestionRepo trackSuggestionRepo;

    @Autowired
    public TrackSuggestionService(TrackSuggestionRepo trackSuggestionRepo){
        this.trackSuggestionRepo = trackSuggestionRepo;
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




}
