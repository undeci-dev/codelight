package com.project.codelight.auth.util;

import com.project.codelight.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenUtils {

    private final SecretKey jwtSecretKey;

    public TokenUtils(@Value("${jwt.secret.key}") String jwtSecretKey) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwt(User user) {
        JwtBuilder builder = Jwts.builder()
                                 .header().add(createHeader()).and()
                                 .claims(createClaims(user))
                                 .subject(String.valueOf(user.getId()))
                                 .signWith(jwtSecretKey)
                                 .expiration(createExpiredDate());
        return builder.compact();
    }

    private static Map<String, Object> createHeader() {
        return Jwts.header()
                   .add("typ", "JWT")
                   .add("alg", "HS256").build();
    }

    private static Map<String, Object> createClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("userEmail", user.getEmail());
        claims.put("userNm", user.getName());
        return claims;
    }

    private static Date createExpiredDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 1);
        return c.getTime();
    }

    public String getClaimsToUserId(String token) {
        Claims claims = getTokenToClaims(token);
        return claims.get("userId").toString();
    }

    public static String getHeaderToToken(String header) {
        return header.split(" ")[1];
    }

    public boolean isValidToken(String token) {
        try {
            Claims claims = Jwts.parser()
                                .verifyWith(jwtSecretKey)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();
            return true;
        } catch (ExpiredJwtException | SignatureException | MalformedJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getTokenToClaims(String token) {
        return Jwts.parser()
                   .verifyWith(jwtSecretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }
}
