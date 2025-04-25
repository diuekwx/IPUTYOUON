package spotify.recommender.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spotify.recommender.Entities.Users;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Long>{
    Optional<Users> findBySpotifyId(String name);

}
