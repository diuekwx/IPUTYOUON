package spotify.recommender.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTService {

    @Value("${keys.secret_key}")
    private String secretKey;

    private Algorithm algorithm;

    private static final long EXPIRATION_TIME_MS = 3600_000;


    @PostConstruct
    public void init(){
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String generateToken(String spotifyId){
        String generatedToken = JWT.create()
                .withClaim("spotify_id", spotifyId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .sign(algorithm);
        return generatedToken;
    }

    public DecodedJWT verifier(String token){
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public String getSpotifyIdFromToken(String token) {
        return verifier(token).getClaim("spotify_id").asString();
    }


}
