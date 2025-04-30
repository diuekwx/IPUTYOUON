package spotify.recommender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import spotify.recommender.Service.JWTService;
import spotify.recommender.Service.SpotifyAuthService;
import spotify.recommender.Service.UserService;

@RestController
@CrossOrigin("http://127.0.0.1:5173")
public class UserController {

    private final SpotifyAuthService authService;
    private final JWTService jwtService;
    private final UserService userService;
    @Autowired
    public UserController(SpotifyAuthService authService, JWTService jwtService, UserService userService) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userService = userService;
    }
}
