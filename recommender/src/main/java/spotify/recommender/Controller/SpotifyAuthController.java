package spotify.recommender.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.Entities.Users;
import spotify.recommender.Service.SpotifyAuthService;
import spotify.recommender.Service.JWTService;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@CrossOrigin("http://localhost:5173")
@RequestMapping("/api/spotify")
public class SpotifyAuthController {

    private final SpotifyAuthService authService;
    private final JWTService jwtService;

    @Value("${frontend.redirect-home-uri}")
    private String homePageUri;


    @Autowired
    public SpotifyAuthController(SpotifyAuthService authService, JWTService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // 1. Step 1 - Redirect user to Spotify login
    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String authUrl = authService.getAuthorizationUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(authUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 redirect
    }

    // 2. Step 2 - Handle Spotify's redirect with the code
    @GetMapping("/callback")
    public RedirectView callback(@RequestParam("code") String code) {
        // return to frontend via httpheader
        try {
            Users user = authService.handleCallback(code);

            String jwt = jwtService.generateToken(user.getSpotify_id());
            // + jwt & go in app.props to add ?token=
            return new RedirectView(homePageUri);

        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView("http://localhost:3000/error?msg=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));        }
    }

    @GetMapping("/me")
    public ResponseEntity<String> whoami(@AuthenticationPrincipal CustomUserPrincipal principal) {
        System.out.println("Done");
        return ResponseEntity.ok("Hello, " + principal.getUser().getSpotify_id());
    }
}

