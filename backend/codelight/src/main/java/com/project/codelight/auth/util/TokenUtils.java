package com.project.codelight.auth.util;

import com.project.codelight.auth.constants.TokenExpiration;
import com.project.codelight.auth.service.model.TokenValidationResult;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
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

    private static SecretKey jwtSecretKey;

    public TokenUtils(@Value("${jwt.secret.key}") String jwtSecretKey) {
        TokenUtils.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateAccessToken(User user) {
        JwtBuilder builder = Jwts.builder()
                                 .header().add(createHeader()).and()
                                 .claims(createClaims(user, true))
                                 .subject(String.valueOf(user.getId()))
                                 .signWith(jwtSecretKey)
                                 .expiration(createExpiredDate(true));
        return builder.compact();
    }

    private static Map<String, Object> createHeader() {
        return Jwts.header()
                   .add("typ", "JWT")
                   .add("alg", "HS256").build();
    }

    private static Map<String, Object> createClaims(User user, boolean isAccessToken) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        if (isAccessToken) {
            claims.put("email", user.getEmail());
            claims.put("name", user.getName());
        }
        return claims;
    }

    public static String getClaimsToUserId(String token) {
        Claims claims = getTokenToClaims(token);
        return claims.get("id").toString();
    }

    public static String getHeaderToToken(String header) {
        return header.split(" ")[1];
    }

    public static TokenValidationResult isValidToken(String token) {
        try {
            getTokenToClaims(token);
            return TokenValidationResult.builder().isValid(true).exceptionCodeTypeName(null)
                                        .build();
        } catch (ExpiredJwtException exception) {
            return TokenValidationResult.builder().isValid(false).exceptionCodeTypeName(
                ExceptionCodeType.TOKEN_EXPIRED.name()).build();
        } catch (JwtException exception) {
            return TokenValidationResult.builder().isValid(false).exceptionCodeTypeName(
                ExceptionCodeType.TOKEN_INVALID.name()).build();
        } catch (NullPointerException exception) {
            return TokenValidationResult.builder().isValid(false).exceptionCodeTypeName(
                ExceptionCodeType.TOKEN_NOT_FOUND.name()).build();
        }
    }

    private static Claims getTokenToClaims(String token) {
        return Jwts.parser()
                   .verifyWith(jwtSecretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    public static String generateRefreshToken(User user) {
        return Jwts.builder()
                   .header().add(createHeader()).and()
                   .claims(createClaims(user, false))
                   .subject(String.valueOf(user.getId()))
                   .signWith(jwtSecretKey)
                   .expiration(createExpiredDate(false))
                   .compact();
    }

    private static Date createExpiredDate(boolean isAccessToken) {
        Calendar c = Calendar.getInstance();
        TokenExpiration expiration = isAccessToken ? TokenExpiration.ACCESS_TOKEN : TokenExpiration.REFRESH_TOKEN;
        c.add(expiration.getCalendarField(), expiration.getValue());
        return c.getTime();
    }

    public static Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setMaxAge((int) TokenExpiration.REFRESH_TOKEN.getExpirationInSeconds());
        refreshCookie.setHttpOnly(true);

        return refreshCookie;
    }

    public static Cookie clearRefreshTokenCookie() {
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setMaxAge(0);
        refreshCookie.setHttpOnly(true);

        return refreshCookie;
    }

    public static User getClaimsToUserDto(String token, boolean isAccessToken) {
        Claims claims = getTokenToClaims(token);
        Long userId = Long.valueOf(claims.get("id").toString());
        if (isAccessToken) {
            String userNm = claims.get("name").toString();
            String email = claims.get("email").toString();
            return User.builder().id(userId).name(userNm).email(email).build();
        } else {
            return User.builder().id(userId).build();
        }
    }

    public static long getClaimsToTTL(String token) {
        Claims claims = getTokenToClaims(token);

        long expMillis = claims.getExpiration().getTime();
        long nowMillis = System.currentTimeMillis();

        long ttlMillis = expMillis - nowMillis;

        return ttlMillis > 0 ? ttlMillis / 1000 : 0;
    }


}
