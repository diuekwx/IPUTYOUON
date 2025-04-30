package spotify.recommender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import spotify.recommender.Service.CustomOAuth2Service;

import java.util.List;



@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${frontend.redirect-home-uri}")
    private String homePageUri;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Allow both localhost and 127.0.0.1 for development flexibility
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // Or be more specific if preferred
        config.setExposedHeaders(List.of("Set-Cookie", "Authorization")); // Expose Set-Cookie header
        config.setAllowCredentials(true); // Allow cookies to be sent

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Explicitly define the SecurityContextRepository bean
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           CustomOAuth2Service customOAuth2UserService,
                                           SecurityContextRepository securityContextRepository) throws Exception { // Inject the repository
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                // Explicitly configure the securityContextRepository
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(securityContextRepository)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/spotify/login", "/api/debug/**").permitAll()
                        // Permit OPTIONS requests to all paths for CORS preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Permit the OAuth2 login initiation and callback paths
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        // Require authentication for all other requests
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl(homePageUri, true)
                )
                .sessionManagement(session -> session
                        // Ensure session is created if needed and used
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // IF_REQUIRED is default and usually sufficient
                );

        return http.build();
    }
}