package spotify.recommender.Service;

//import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.UserRepo;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;




    private final UserRepo userRepo;
    @Autowired
    public SpotifyAuthService(UserRepo userRepo){
        this.userRepo = userRepo;
    }


    public String getAuthorizationUrl() {
        String scope = "playlist-modify-public playlist-modify-private";
        String url = "https://accounts.spotify.com/authorize" +
                "?client_id=" + clientId +
                "&response_type=code" +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&show_dialog=True";
        System.out.println("Generated Spotify Auth URL: " + url);
        return url;

    }

    //requesting access token
    public Users handleCallback(String code) throws IOException {
        // Required HTTP Headers - authorization:basic base64(client_id:client_secret)
        // urlencoded application urlencoded
        HttpHeaders headers = new HttpHeaders();
        String authHeader = clientId + ":" + clientSecret;
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(authHeader.getBytes()));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //token request -> token endpoint // prep token request
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        // send POST token request // actually sending the request to get details
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        //Will send the request
        RestTemplate restTemplate = new RestTemplate();
        //Token endpoint, body&headers, the object we want back
        ResponseEntity<Map> response = restTemplate.postForEntity("https://accounts.spotify.com/api/token", request, Map.class);

        //extract tokens
        String accessToken = (String) response.getBody().get("access_token");
        String refreshToken = (String) response.getBody().get("refresh_token");

        //expire token
        Integer expiresIn = (Integer) response.getBody().get("expires_in"); // in seconds
        LocalDateTime tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn);

        // Get user profile info from Spotify
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(accessToken);
        HttpEntity<String> authRequest = new HttpEntity<>(authHeaders);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://api.spotify.com/v1/me", HttpMethod.GET, authRequest, Map.class
        );

        String spotifyId = (String) userInfoResponse.getBody().get("id");

        // Save or update user in database
        Users user = userRepo.findBySpotifyId(spotifyId)
                .orElse(new Users());
        user.setSpotify_id(spotifyId);
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user.setTokenExpiry(tokenExpiry);


        return userRepo.save(user);
    }

    //refresh tokens
    public String refreshAccessToken(Users user) {
        HttpHeaders headers = new HttpHeaders();
        String creds = clientId + ":" + clientSecret;
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString(creds.getBytes()));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", user.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity("https://accounts.spotify.com/api/token", request, Map.class);

        String newAccessToken = (String) response.getBody().get("access_token");
        user.setAccessToken(newAccessToken);
        userRepo.save(user);

        return newAccessToken;
    }
    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }


}
