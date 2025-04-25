package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.PlaylistRepo;

import java.util.List;

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
        return playlistRepo.findByUserOwner(name);
    }

    public Playlist savePlaylist(Playlist playlist) {
        return playlistRepo.save(playlist);
    }


}
