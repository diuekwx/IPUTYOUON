package spotify.recommender;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spotify.recommender.Entities.Users;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomUserPrincipal implements OAuth2User {

    private final Users user;
    private final Map<String, Object> attributes;

    //tying auth to user entity
    public CustomUserPrincipal(Users user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public Users getUser() {
        return user;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getSpotify_id();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Simple default authority
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

}