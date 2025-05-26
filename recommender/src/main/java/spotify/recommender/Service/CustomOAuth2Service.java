package spotify.recommender.Service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.UserRepo;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import java.time.Instant;


// leave setting tokens to handler, just have this map the user to a principal
// i.e. create or load user entity based on database, wrap it into object, spring will authenticate user for app

@Service
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepo userRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;



    public CustomOAuth2Service(UserRepo userRepository, UserService userService, EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.encryptionService = encryptionService;
    }


    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(request);


        String spotifyId = oauth2User.getAttribute("id"); // This is the unique ID
        String displayName = oauth2User.getAttribute("display_name"); // This is the display name
        String accessToken = request.getAccessToken().getTokenValue();

        String encrypted = encryptionService.encryptSafe(accessToken);
//        try {
//            encrypted = encryptionService.encrypted(accessToken);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Encryption failed", e);
//        }


        Users user = userRepository.findBySpotifyId(spotifyId).orElse(null);

        if (user == null) {
            user = new Users();
            user.setSpotify_id(spotifyId);


        }
        user.setAccessToken(encrypted);
        // can change
        user.setDisplayName(displayName);
        // update the access token  might have been refreshed


        // Get token expiry (available on OAuth2AccessToken)
        Instant tokenExpiry = request.getAccessToken().getExpiresAt();
        if (tokenExpiry != null) {
            user.setTokenExpiry(tokenExpiry);
        } else {
            // If column is nullable and expiry is somehow null (unlikely for access tokens), set to null
            user.setTokenExpiry(null);
        }

        userRepository.save(user); // Save the created or updated user

        return new CustomUserPrincipal(user, oauth2User.getAttributes());
    }




}