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
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
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
                ExceptionCodeType.TOKEN_EXPIRED.getExceptionCode().name()).build();
        } catch (JwtException exception) {
            return TokenValidationResult.builder().isValid(false).exceptionCodeTypeName(
                ExceptionCodeType.TOKEN_INVALID.getExceptionCode().name()).build();
        } catch (NullPointerException exception) {
            return TokenValidationResult.builder().isValid(false).exceptionCodeTypeName(
                ExceptionCodeType.TOKEN_NOT_FOUND.getExceptionCode().name()).build();
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

    public static ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                             .maxAge(TokenExpiration.REFRESH_TOKEN.getExpirationInSeconds())
//                             .httpOnly(true) // 개발용(임시)
                             .path("/")
                             .sameSite("Lax")  // 개발용(임시)
                             .secure(false)     // 개발용(임시)
                             // .secure(true)  // 개발용(임시) - HTTPS 적용 후 풀기
                             .build();
    }

    public static ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", null)
                             .maxAge(0)
//                             .httpOnly(true) // 개발용(임시)
                             .path("/")
                             .sameSite("Lax")  // 개발용(임시)
                             .secure(false)     // 개발용(임시)
                             // .secure(true)  // 개발용(임시) - HTTPS 적용 후 풀기
                             .build();
    }

    public static User getClaimsToUserDto(String token) {
        Claims claims = getTokenToClaims(token);
        Long userId = Long.valueOf(claims.get("id").toString());
        String userNm = claims.get("name").toString();
        String email = claims.get("email").toString();
        return User.builder().id(userId).name(userNm).email(email).build();
    }

    public static long getClaimsToTTL(String token) {
        Claims claims = getTokenToClaims(token);

        long expMillis = claims.getExpiration().getTime();
        long nowMillis = System.currentTimeMillis();

        long ttlMillis = expMillis - nowMillis;

        return ttlMillis > 0 ? ttlMillis / 1000 : 0;
    }


}
