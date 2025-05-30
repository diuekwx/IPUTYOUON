package spotify.recommender.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;
import spotify.recommender.Entities.Playlist;
import spotify.recommender.Entities.Users;

import java.util.List;

@Repository
public interface PlaylistRepo extends JpaRepository<Playlist, Long> {

    Playlist findBySpotifyPlaylistId(String id);

    List<Playlist> findAllByUserOwner(Users name);

    void deleteByUserOwner(Users name);


    // cant use ORDER BY RANDOM() since it scans full table and random sorts...well probably could no ones gonna use this shit anyway lol
    @Query("SELECT p.spotifyPlaylistId FROM Playlist p WHERE p.rowIndex IN :indexes AND p.userOwner != :user" )
    List<String> getRandomFeed(@Param("user") Users user, @Param("indexes") List<Integer> indexes);

    @Query("SELECT DISTINCT MAX(p.rowIndex) FROM Playlist p")
    int getMaxRowIndex();


}
