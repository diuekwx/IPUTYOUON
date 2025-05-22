package spotify.recommender;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import spotify.recommender.Service.UserService;

import java.io.IOException;



@Component // Make it a Spring component so you can inject it
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Value("${frontend.redirect-home-uri}")
    private String homePageUri;

    private final UserService userService; // Inject your UserService

    @Autowired
    public CustomOAuth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // "spotify" is the clientRegistrationId
        System.out.println("Found Call!");
        userService.saveOrUpdateWithTokens(authentication, "spotify");


        System.out.println("OAuth2 Authentication Success! Tokens should be saved.");

        response.sendRedirect(homePageUri);

    }
}