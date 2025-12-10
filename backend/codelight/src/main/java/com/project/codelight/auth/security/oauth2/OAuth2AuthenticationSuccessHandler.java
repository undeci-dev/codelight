package com.project.codelight.auth.security.oauth2;

import com.project.codelight.auth.constants.TokenExpiration;
import com.project.codelight.auth.domain.RefreshToken;
import com.project.codelight.auth.repository.RefreshTokenRepository;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${cors.allowed-origins[0]}")
    private String frontendUrl;

    @Value("${oauth.callback-path}")
    private String callbackPath;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        String refreshToken = TokenUtils.generateRefreshToken(user);

        saveRefreshToken(user.getId(), refreshToken);

        ResponseCookie refreshCookie = TokenUtils.createRefreshTokenCookie(refreshToken);
        response.addHeader("Set-Cookie", refreshCookie.toString());

        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + callbackPath)
            .queryParam("isNewUser", oAuth2User.isNewUser())
            .build()
            .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
            .userId(userId)
            .token(refreshToken)
            .expiration(TokenExpiration.REFRESH_TOKEN.getExpirationInSeconds())
            .build();

        refreshTokenRepository.save(token);
    }
}
