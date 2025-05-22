package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.PlaylistRepo;

import java.util.*;

@Service
public class PlaylistService {

    private PlaylistRepo playlistRepo;

    @Autowired
    public PlaylistService(PlaylistRepo playlistRepo){
        this.playlistRepo = playlistRepo;
    }

    public Playlist getPlaylist(String id){
        return playlistRepo.findBySpotifyPlaylistId(id);
    }

    public List<Playlist> getUsersPlaylist(Users name){
        return playlistRepo.findAllByUserOwner(name);
    }

    public List<Integer> getRandomIndexes(int count){
        Random r = new Random();
        Set<Integer> result = new HashSet<>();
        while (result.size() < count) {
            int next = 1 + r.nextInt(playlistRepo.getMaxRowIndex()); // 1 to max inclusive
            result.add(next);
        }
        return new ArrayList<>(result);
    }


    public List<String> getUserFeed(Users user, int sampleSize){
        List<Integer> indexes = getRandomIndexes(sampleSize);
        return playlistRepo.getRandomFeed(user, indexes);
    }

    public Playlist savePlaylist(Playlist playlist) {
        return playlistRepo.save(playlist);
    }

}
