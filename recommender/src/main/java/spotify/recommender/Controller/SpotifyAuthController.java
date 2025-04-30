package spotify.recommender.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("http://127.0.0.1:5173")
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

    @RestController
    public class AuthTestController {
        @GetMapping("/api/auth-test")
        public ResponseEntity<String> testAuth(@AuthenticationPrincipal CustomUserPrincipal principal) {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Not authenticated");
            }
            return ResponseEntity.ok("Authenticated as: " + principal.getUser().getSpotify_id());
        }
    }

    @GetMapping("/api/debug/auth-status")
    public ResponseEntity<Map<String, Object>> getAuthStatus(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            HttpServletRequest request) {

        Map<String, Object> status = new HashMap<>();

        // Check authentication principal
        status.put("principalExists", principal != null);
        if (principal != null) {
            status.put("userId", principal.getUser().getSpotify_id());
        }

        // Check session
        HttpSession session = request.getSession(false);
        status.put("sessionExists", session != null);
        if (session != null) {
            status.put("sessionId", session.getId());
        }

        // Check cookies
        Cookie[] cookies = request.getCookies();
        status.put("hasCookies", cookies != null && cookies.length > 0);

        return ResponseEntity.ok(status);
    }
    @GetMapping("/me")
    public ResponseEntity<String> whoami(@AuthenticationPrincipal CustomUserPrincipal principal) {
        System.out.println("Done");
        return ResponseEntity.ok("Hello, " + principal.getUser().getSpotify_id());
    }
}

