package kr.doridos.ticketservice.ticket.fixture;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class TokenFixture {

    static String secretKey = "TESTGUYGUYGKUYEOUIPWOEIDUFUHIUHOIUHOUIHOIUHOUHDVDNHLADSUHFWEWEQWERWQgI3nVPEahqxeY8qbPSFGyzyEVxnl4AQcrnVneI";
    static byte[] bytes = Base64.getDecoder().decode(secretKey);
    static Key key = Keys.hmacShaKeyFor(bytes);

    public static String 티켓매니저_토큰_생성() {
        Date now = new Date();
        return Jwts.builder()
                .setSubject("email@naver.com")
                .claim("Role", "TICKET_MANAGER")
                .claim("userId", 1L)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 20000000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String 티켓매니저2_토큰_생성() {
        Date now = new Date();
        return Jwts.builder()
                .setSubject("email@naver.com")
                .claim("Role", "TICKET_MANAGER")
                .claim("userId", 2L)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 20000000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String 일반유저_토큰_생성() {
        Date now = new Date();
        return Jwts.builder()
                .setSubject("email@naver.com")
                .claim("Role", "USER")
                .claim("userId", 1L)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + 20000000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
