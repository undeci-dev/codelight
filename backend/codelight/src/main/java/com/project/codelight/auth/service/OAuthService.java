package com.project.codelight.auth.service;

import com.project.codelight.auth.domain.OAuthAccount;
import com.project.codelight.auth.domain.OAuthProvider;
import com.project.codelight.auth.domain.RefreshToken;
import com.project.codelight.auth.dto.response.KakaoLoginResponse;
import com.project.codelight.auth.dto.response.KakaoTokenResponse;
import com.project.codelight.auth.dto.response.KakaoUserInfoResponse;
import com.project.codelight.auth.repository.AuthRepository;
import com.project.codelight.auth.repository.OAuthAccountRepository;
import com.project.codelight.auth.repository.RefreshTokenRepository;
import com.project.codelight.auth.service.model.KakaoApiClient;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import com.project.codelight.user.domain.UserRole;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private final KakaoApiClient kakaoApiClient;
    private final AuthRepository authRepository;
    private final OAuthAccountRepository oauthAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public KakaoLoginResponse login(String code) {
        KakaoTokenResponse tokenResponse = kakaoApiClient.getAccessToken(code);

        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(
            tokenResponse.getAccessToken());

        String email = userInfo.getEmail();
        if (email == null || email.isBlank()) {
            throw new CodeLightException(ExceptionCodeType.OAUTH_EMAIL_NOT_PROVIDED);
        }

        User user = processOAuthUser(userInfo, tokenResponse);

        String accessToken = TokenUtils.generateAccessToken(user);
        String refreshToken = TokenUtils.generateRefreshToken(user);

        saveRefreshToken(user.getId(), refreshToken);

        return KakaoLoginResponse.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .isNewUser(user.getCreatedAt().equals(user.getUpdatedAt()))
            .build();
    }

    private User processOAuthUser(KakaoUserInfoResponse userInfo, KakaoTokenResponse tokenResponse) {
        String providerUserId = String.valueOf(userInfo.getId());
        OAuthProvider provider = OAuthProvider.KAKAO;

        return oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
            .map(oauthAccount -> {
                updateOAuthAccountTokens(oauthAccount, userInfo, tokenResponse);
                return handleExistingUser(oauthAccount.getUser());
            })
            .orElseGet(() -> {
                return createNewOAuthUser(userInfo, tokenResponse, provider, providerUserId);
            });
    }

    private User handleExistingUser(User user) {
        if (user.isDeleted()) {
            authRepository.restoreUserByEmailAndLoginType(user.getEmail(), LoginType.OAUTH);
            return authRepository.findUserIncludingDeletedByEmailAndLoginType(user.getEmail(),
                    LoginType.OAUTH)
                .orElseThrow(() -> new CodeLightException(ExceptionCodeType.USER_NOT_FOUND));
        }
        return user;
    }

    private User createNewOAuthUser(KakaoUserInfoResponse userInfo,
                                    KakaoTokenResponse tokenResponse,
                                    OAuthProvider provider,
                                    String providerUserId) {
        String email = userInfo.getEmail();
        String nickname = userInfo.getNickname();

        User newUser = User.builder()
            .email(email)
            .name(nickname != null ? nickname : email.split("@")[0])
            .password(null)
            .userRole(UserRole.USER)
            .loginType(LoginType.OAUTH)
            .build();

        User savedUser = authRepository.save(newUser);

        LocalDateTime tokenExpiresAt = LocalDateTime.now()
            .plusSeconds(tokenResponse.getExpiresIn() != null ? tokenResponse.getExpiresIn() : 0);

        OAuthAccount oauthAccount = OAuthAccount.builder()
            .user(savedUser)
            .provider(provider)
            .providerUserId(providerUserId)
            .providerEmail(email)
            .providerName(nickname)
            .accessToken(tokenResponse.getAccessToken())
            .refreshToken(tokenResponse.getRefreshToken())
            .tokenExpiresAt(tokenExpiresAt)
            .build();

        oauthAccountRepository.save(oauthAccount);

        return savedUser;
    }

    private void updateOAuthAccountTokens(OAuthAccount oauthAccount,
                                          KakaoUserInfoResponse userInfo,
                                          KakaoTokenResponse tokenResponse) {
        LocalDateTime tokenExpiresAt = LocalDateTime.now()
            .plusSeconds(tokenResponse.getExpiresIn() != null ? tokenResponse.getExpiresIn() : 0);

        oauthAccount.updateTokens(
            tokenResponse.getAccessToken(),
            tokenResponse.getRefreshToken(),
            tokenExpiresAt
        );

        oauthAccount.updateProviderInfo(
            userInfo.getEmail(),
            userInfo.getNickname()
        );

        oauthAccountRepository.save(oauthAccount);
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
            .userId(userId)
            .token(refreshToken)
            .build();

        refreshTokenRepository.save(token);
    }
}
