package com.project.codelight.auth.service;

import com.project.codelight.auth.dto.request.SignUpRequest;
import com.project.codelight.auth.repository.AuthRepository;
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

    private final PasswordEncoder pwEncoder;

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
}