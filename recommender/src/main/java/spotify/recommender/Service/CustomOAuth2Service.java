package spotify.recommender.Service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import spotify.recommender.CustomUserPrincipal;
import spotify.recommender.Entities.Users;
import spotify.recommender.Repository.UserRepo;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

@Service
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepo userRepository;

    public CustomOAuth2Service(UserRepo userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(request);

        String spotifyId = oauth2User.getAttribute("id");
        String displayName = oauth2User.getAttribute("display_name");
        String accessToken = request.getAccessToken().getTokenValue();

        Users user = userRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> new Users(spotifyId));

        user.setSpotify_id(displayName);
        user.setAccessToken(accessToken);
        userRepository.save(user);

        // This lets Spring inject it with @AuthenticationPrincipal
        return new CustomUserPrincipal(user, oauth2User.getAttributes());
    }


}