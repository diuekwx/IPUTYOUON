package spotify.recommender.Service;

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

@Service
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepo userRepository;

    public CustomOAuth2Service(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(request);

        String spotifyId = oauth2User.getAttribute("id"); // This is the unique ID
        String displayName = oauth2User.getAttribute("display_name"); // This is the display name
        String accessToken = request.getAccessToken().getTokenValue();

        // Attempt to find existing user by the unique Spotify ID
        Users user = userRepository.findBySpotifyId(spotifyId).orElse(null);

        if (user == null) {
            // User does not exist, create a new one
            user = new Users();
            user.setSpotify_id(spotifyId); // <-- Set the unique Spotify ID here!
//            user.setDisplayName(displayName); // <-- Set the display name in a different field if you have one
            // Set other initial properties if needed
        } else {
            // User exists, update their display name if necessary (display names can change)
//            user.setDisplayName(displayName); // <-- Update display name if you have a field for it
        }

        // Always update the access token as it might have been refreshed
        user.setAccessToken(accessToken);

        // Handle refresh token and expiry (based on whether columns are nullable or if data is needed)
        // Get refresh token (might be null if not issued or not configured)
//        OAuth2RefreshToken refreshToken = request.getOAuth2AccessToken().getRefreshToken(); // This is null on OAuth2AccessToken itself, need to get from authorized client
        // **Correction:** Refresh token is NOT directly on OAuth2AccessToken.
        // You need to access the OAuth2AuthorizedClient or the Authentication object after this method.
        // For now, handle nullable column or fetch it later.

        // Get token expiry (available on OAuth2AccessToken)
        Instant tokenExpiry = request.getAccessToken().getExpiresAt();
        if (tokenExpiry != null) {
            user.setTokenExpiry(tokenExpiry);
        } else {
            // If column is nullable and expiry is somehow null (unlikely for access tokens), set to null
            user.setTokenExpiry(null);
        }

        // *** Regarding Refresh Token: ***
        // Getting the refresh token here directly from OAuth2UserRequest is not standard.
        // You will likely need to access it after this loadUser method completes,
        // possibly by injecting OAuth2AuthorizedClientService and retrieving the
        // OAuth2AuthorizedClient associated with the authenticated user.
        // For now, rely on the column being nullable if the refresh token is null.


        userRepository.save(user); // Save the created or updated user

        // This lets Spring inject it with @AuthenticationPrincipal
        return new CustomUserPrincipal(user, oauth2User.getAttributes());
    }


}