package spotify.recommender.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;

import java.util.List;

@Repository
public interface PlaylistRepo extends JpaRepository<Playlist, Long> {

    Playlist findBySpotifyPlaylistId(String name);

    List<Playlist> findAllByUserOwner(Users name);

    void deleteByUserOwner(Users name);


}
