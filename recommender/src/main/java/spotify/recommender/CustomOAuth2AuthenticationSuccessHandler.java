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

        // Call the method from your UserService to save/update tokens
        // "spotify" is the clientRegistrationId
        System.out.println("Found Call!");
        userService.saveOrUpdateWithTokens(authentication, "spotify");

        // Continue with the default behavior (e.g., redirecting to defaultSuccessUrl)
        // If you don't call super.onAuthenticationSuccess or handle the redirect yourself,
        // the user might not be redirected after login.
        // A simple way is to let Spring's default handler do the redirect after your logic.
        // You might need to inject the DefaultRedirectStrategy or similar if you handle redirect manually.
        // For simplicity, often you'd just perform your saving logic and let the default
        // success URL handling (configured via .defaultSuccessUrl) take over.
        // If you need more control over the redirect, you'd implement that here.

        // Example: If you want to explicitly redirect:
        // new DefaultRedirectStrategy().sendRedirect(request, response, "/some-other-url");

        // If you just want to perform the saving and let defaultSuccessUrl handle redirect:
        // The method signature requires throws, but you might not need to throw here
        // if you are just performing side effects (saving).
        System.out.println("OAuth2 Authentication Success! Tokens should be saved.");

        response.sendRedirect(homePageUri);

    }
}