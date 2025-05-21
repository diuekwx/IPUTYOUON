package spotify.recommender.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.UserRepo;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {

    private UserRepo userRepo;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    public UserService(UserRepo userRepo, OAuth2AuthorizedClientService authorizedClient){
        this.userRepo = userRepo;
        this.authorizedClientService = authorizedClient;
    }

    public Users createUsers(Users user){
        return userRepo.save(user);
    }

    public Optional<Users> getUser(String user){
        return userRepo.findBySpotifyId(user);
    }

    public void saveOrUpdateWithTokens(Authentication auth, String clientRegistration){
        System.out.println("actiavtion");
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
            // Now you have the authorized client, you can access the tokens
            String accessTokenValue = authorizedClient.getAccessToken().getTokenValue();
            Instant accessTokenExpiry = authorizedClient.getAccessToken().getExpiresAt();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken(); // Get the refresh token object

            // Get the refresh token string value if the refresh token exists
            String refreshTokenValue = (refreshToken != null) ? refreshToken.getTokenValue() : null;
            System.out.println(refreshTokenValue);
            // Update your Users entity with the token information
            if (user != null) {
                user.setAccessToken(accessTokenValue);
                user.setTokenExpiry(accessTokenExpiry);
                user.setRefreshToken(refreshTokenValue); // Save the refresh token string

                userRepo.save(user); // Save the updated user entity
                System.out.println("User tokens saved/updated for user: " + principalName);
            } else {
                System.err.println("User entity not found for principal: " + principalName);
            }

        } else {
            System.err.println("OAuth2AuthorizedClient not found for principal: " + principalName + " and client: " + clientRegistration);
            // This might happen if the authorization failed or wasn't completed correctly
        }

    }


    public Users saveUser(Users user){
        System.out.println(user);
        return userRepo.save(user);
    }

}
