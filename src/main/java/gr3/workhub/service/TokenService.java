package gr3.workhub.service;

import gr3.workhub.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtil jwtUtil;

    public Integer extractUserIdFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            org.springframework.security.oauth2.jwt.Jwt decoded = jwtUtil.decodeToken(token);
            Object idClaim = decoded.getClaim("id");
            if (idClaim == null) {
                throw new IllegalArgumentException("JWT does not contain user id");
            }
            return Integer.parseInt(idClaim.toString());
        }
        throw new IllegalArgumentException("Invalid or missing Authorization header");
    }

    public String extractUserRoleFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return jwtUtil.getRoleFromToken(token);
        }
        throw new IllegalArgumentException("Invalid or missing Authorization header");
    }
}