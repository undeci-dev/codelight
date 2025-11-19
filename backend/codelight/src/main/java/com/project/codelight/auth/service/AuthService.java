package com.project.codelight.auth.service;

import com.project.codelight.auth.domain.RefreshToken;
import com.project.codelight.auth.domain.TokenBlackList;
import com.project.codelight.auth.dto.request.SignUpRequest;
import com.project.codelight.auth.dto.response.ReissueTokenResponse;
import com.project.codelight.auth.repository.AuthRepository;
import com.project.codelight.auth.repository.RefreshTokenRepository;
import com.project.codelight.auth.repository.TokenBlackListRepository;
import com.project.codelight.auth.service.dto.TokenValidationResult;
import com.project.codelight.auth.util.TokenUtils;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder pwEncoder;
    private final TokenBlackListRepository tokenBlackListRepository;

    @Transactional
    public Long register(SignUpRequest request) {
        User user = processUser(LoginType.LOCAL, request.toUserEntity());
        return user.getId();
    }

    private User processUser(LoginType loginType, User user) {
        return authRepository.findUserIncludingDeletedByEmailAndLoginType(user.getEmail(),
                                 loginType)
                             .map(savedUser -> verifyExistingUserStatus(savedUser, loginType))
                             .orElseGet(() -> {
                                 String encodedPassword = pwEncoder.encode(user.getPassword());
                                 User newUser = User.builder()
                                                    .name(user.getName())
                                                    .email(user.getEmail())
                                                    .password(encodedPassword)
                                                    .userRole(user.getUserRole())
                                                    .loginType(user.getLoginType()).build();
                                 return authRepository.save(newUser);
                             });
    }

    private User verifyExistingUserStatus(User user, LoginType loginType) {
        validateRegister(user);
        restoreUser(user, loginType);
        return user;
    }

    private void validateRegister(User user) {
        if (!user.isDeleted()) {
            throw new CodeLightException(ExceptionCodeType.USER_EMAIL_ALREADY_USED);
        }
    }

    private void restoreUser(User user, LoginType loginType) {
        if (user.isDeleted()) {
            authRepository.restoreUserByEmailAndLoginType(user.getEmail(), loginType);
        }
    }

    @Transactional
    public ReissueTokenResponse reissueToken(String refreshToken) {
        TokenValidationResult tokenValidationResult = TokenUtils.isValidToken(refreshToken);
        if (!tokenValidationResult.isValid()) {
            throw new CodeLightException(
                ExceptionCodeType.valueOf(tokenValidationResult.getExceptionCodeTypeName()));
        }

        String userId = TokenUtils.getClaimsToUserId(refreshToken);

        // Redis에서 RefreshToken 조회 및 검증
        RefreshToken storedRefreshToken = refreshTokenRepository.findById(Long.valueOf(userId))
                                                                .orElseThrow(
                                                                    () -> new CodeLightException(
                                                                        ExceptionCodeType.TOKEN_INVALID));

        // Redis에 저장된 토큰과 요청된 토큰 비교
        if (!storedRefreshToken.getToken().equals(refreshToken)) {
            throw new CodeLightException(ExceptionCodeType.TOKEN_INVALID);
        }

        User user = authRepository.findById(Long.valueOf(userId))
                                  .orElseThrow(() -> new CodeLightException(
                                      ExceptionCodeType.USER_NOT_FOUND));

        if (user.isDeleted()) {
            throw new CodeLightException(ExceptionCodeType.USER_ACCOUNT_DELETED);
        }

        String newAccessToken = TokenUtils.generateAccessToken(user);
        String newRefreshToken = TokenUtils.generateRefreshToken(user);

        // Redis에 새로운 RefreshToken 업데이트
        RefreshToken updatedRefreshToken = storedRefreshToken.updateToken(newRefreshToken);
        refreshTokenRepository.save(updatedRefreshToken);

        return ReissueTokenResponse.builder()
                                   .userId(Long.valueOf(userId))
                                   .accessToken(newAccessToken)
                                   .refreshToken(newRefreshToken)
                                   .build();
    }

    public void addTokenBlackList(String accessToken) {
        TokenBlackList tokenBlackList = TokenBlackList.builder()
                                                      .token(accessToken)
                                                      .expiration(TokenUtils.getClaimsToTTL(
                                                          accessToken))
                                                      .build();

        tokenBlackListRepository.save(tokenBlackList);
    }

    public void removeRefreshToken(String accessToken) {
        String userId = TokenUtils.getClaimsToUserId(accessToken);
        refreshTokenRepository.findById(Long.valueOf(userId))
                              .ifPresent(refreshTokenRepository::delete);
    }
}