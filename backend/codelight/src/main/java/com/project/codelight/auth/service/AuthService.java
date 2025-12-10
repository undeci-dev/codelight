package com.project.codelight.auth.service;

import com.project.codelight.auth.domain.RefreshToken;
import com.project.codelight.auth.domain.TokenBlackList;
import com.project.codelight.auth.dto.response.ReissueTokenResponse;
import com.project.codelight.auth.repository.AuthRepository;
import com.project.codelight.auth.repository.RefreshTokenRepository;
import com.project.codelight.auth.repository.TokenBlackListRepository;
import com.project.codelight.auth.service.model.TokenValidationResult;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlackListRepository tokenBlackListRepository;

    @Transactional
    public ReissueTokenResponse reissueToken(String refreshToken) {
        TokenValidationResult tokenValidationResult = TokenUtils.isValidToken(refreshToken);
        if (!tokenValidationResult.isValid()) {
            throw new CodeLightException(
                ExceptionCodeType.valueOf(tokenValidationResult.getExceptionCodeTypeName()));
        }

        String userId = TokenUtils.getClaimsToUserId(refreshToken);

        RefreshToken storedRefreshToken = refreshTokenRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new CodeLightException(ExceptionCodeType.TOKEN_INVALID));

        if (!storedRefreshToken.getToken().equals(refreshToken)) {
            throw new CodeLightException(ExceptionCodeType.TOKEN_INVALID);
        }

        User user = authRepository.findById(Long.valueOf(userId))
            .orElseThrow(() -> new CodeLightException(ExceptionCodeType.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CodeLightException(ExceptionCodeType.USER_ACCOUNT_DELETED);
        }

        String newAccessToken = TokenUtils.generateAccessToken(user);
        String newRefreshToken = TokenUtils.generateRefreshToken(user);

        RefreshToken updatedRefreshToken = storedRefreshToken.updateToken(newRefreshToken);
        refreshTokenRepository.save(updatedRefreshToken);

        return ReissueTokenResponse.builder()
            .userId(Long.valueOf(userId))
            .accessToken("Bearer " + newAccessToken)
            .refreshToken(newRefreshToken)
            .build();
    }

    public void logout(String accessToken) {
        addTokenBlackList(accessToken);
        removeRefreshToken(accessToken);
    }

    private void addTokenBlackList(String accessToken) {
        TokenBlackList tokenBlackList = TokenBlackList.builder()
            .token(accessToken)
            .expiration(TokenUtils.getClaimsToTTL(accessToken))
            .build();

        tokenBlackListRepository.save(tokenBlackList);
    }

    private void removeRefreshToken(String accessToken) {
        String userId = TokenUtils.getClaimsToUserId(accessToken);
        refreshTokenRepository.findById(Long.valueOf(userId))
            .ifPresent(refreshTokenRepository::delete);
    }
}
