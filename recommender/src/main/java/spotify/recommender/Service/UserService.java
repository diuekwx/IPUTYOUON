package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.UserRepo;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private UserRepo userRepo;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final EncryptionService encryptionService;

    @Autowired
    public UserService(UserRepo userRepo, OAuth2AuthorizedClientService authorizedClient, EncryptionService encryptionService){
        this.userRepo = userRepo;
        this.authorizedClientService = authorizedClient;
        this.encryptionService = encryptionService;
    }

    public Users createUsers(Users user){
        return userRepo.save(user);
    }

    public Optional<Users> getUser(String user){
        return userRepo.findBySpotifyId(user);
    }

    public void saveOrUpdateWithTokens(Authentication auth, String clientRegistration){

        Object principal = auth.getPrincipal();
        String principalName = null;

        Users user = null;

        if (principal instanceof CustomUserPrincipal){
            CustomUserPrincipal customUserPrincipal = (CustomUserPrincipal) principal;
            user = customUserPrincipal.getUser();
            principalName = user.getSpotify_id();
        }
        else {

            System.err.println("Unexpected principal type: " + principal.getClass().getName());
            return; // Or throw an exception
        }

        if (principalName == null) {
            System.err.println("Could not determine principal name for authorized client lookup.");
            return;
        }
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                clientRegistration, // e.g., "spotify" - must match your client registration ID
                principalName         // The unique identifier of the authenticated user
        );

        if (authorizedClient != null) {
            // access tokens in authorized client
            String accessTokenValue = authorizedClient.getAccessToken().getTokenValue();
            String encryptedAccessToken = encryptionService.encryptSafe(accessTokenValue);

            Instant accessTokenExpiry = authorizedClient.getAccessToken().getExpiresAt();

            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            String refreshTokenValue = (refreshToken != null) ? encryptionService.encryptSafe(refreshToken.getTokenValue()) : null;

            if (user != null) {
                user.setAccessToken(encryptedAccessToken);
                user.setTokenExpiry(accessTokenExpiry);
                user.setRefreshToken(refreshTokenValue); // Save the refresh token string

                userRepo.save(user); // Save the updated user entity
                System.out.println("User tokens saved/updated for user: " + principalName);
            } else {
                System.err.println("User entity not found for principal: " + principalName);
            }

        } else {
            System.err.println("OAuth2AuthorizedClient not found for principal: " + principalName + " and client: " + clientRegistration);
            //  might happen if the authorization failed or wasn't completed correctly
        }

    }

    //fallback for oauth2 not getting it
    public String getDisplayName(Users user){
        String accessToken = user.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(headers);
        try{
            ResponseEntity<Map> response = new RestTemplate().exchange(
                    "https://api.spotify.com/v1/me",
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            Map<String, Object> res = response.getBody();
            Object name = res.get("display_name");
            if (name != null && name instanceof String){
                return (String) name;
            }

        }
        catch (HttpClientErrorException e){
            return null;
        }
        return null;
    }


    public Users saveUser(Users user){
        System.out.println(user);
        return userRepo.save(user);
    }

}
