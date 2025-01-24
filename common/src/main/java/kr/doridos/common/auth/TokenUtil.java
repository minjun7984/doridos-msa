package kr.doridos.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Slf4j
@Component
public class TokenUtil {

    @Value("${auth.token.secretKey}")
    private String secretKey;

    public Claims extractAllClaims(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
        return parser.parseClaimsJws(token).getBody();
    }

    public UserInfo extractUserInfo(String token) {
        Claims claims = extractAllClaims(token);
        Long userId = claims.get("userId", Long.class);
        String userType = claims.get("Role", String.class);
        log.info("userType{}", userType);
        log.info("userId{}", userId);
        return new UserInfo(userId, userType);
    }

    private Key getSigningKey() {
        byte[] secretKeyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }
}
