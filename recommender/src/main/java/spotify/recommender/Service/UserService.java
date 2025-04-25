package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.UserRepo;

import java.util.Optional;

@Service
public class UserService {

    private UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    public Users createUsers(Users user){
        return userRepo.save(user);
    }

    public Optional<Users> getUser(String user){
        return userRepo.findBySpotifyId(user);
    }

}
