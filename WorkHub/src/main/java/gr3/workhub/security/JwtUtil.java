package gr3.workhub.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtDecoder jwtDecoder;

    public JwtUtil(@Value("${jwt.secret}") String secretKeyString) {
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(
                new javax.crypto.spec.SecretKeySpec(secretKeyString.getBytes(), "HmacSHA256")
        ).build();
    }

    public Jwt decodeToken(String token) {
        return jwtDecoder.decode(token);
    }

    public String getUsernameFromToken(String token) {
        Jwt jwt = decodeToken(token);
        return jwt.getSubject();
    }

    public String getRoleFromToken(String token) {
        Jwt jwt = decodeToken(token);
        Object role = jwt.getClaim("role");
        return role != null ? role.toString() : null;
    }

    public boolean validateToken(String token) {
        try {
            decodeToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}