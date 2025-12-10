package com.project.codelight.auth.security.oauth2;

import com.project.codelight.auth.domain.OAuthAccount;
import com.project.codelight.auth.domain.OAuthProvider;
import com.project.codelight.auth.repository.AuthRepository;
import com.project.codelight.auth.repository.OAuthAccountRepository;
import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import com.project.codelight.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthRepository authRepository;
    private final OAuthAccountRepository oauthAccountRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest);

            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

            OAuth2Attributes attributes = OAuth2Attributes.of(
                registrationId,
                userNameAttributeName,
                oAuth2User.getAttributes()
            );

            if (attributes.getEmail() == null || attributes.getEmail().isBlank()) {
                throw new CodeLightException(ExceptionCodeType.OAUTH_EMAIL_NOT_PROVIDED);
            }

            UserResult userResult = saveOrUpdate(attributes);

            return new CustomOAuth2User(
                userResult.user(),
                attributes.getAttributes(),
                attributes.getNameAttributeKey(),
                userResult.isNewUser()
            );
        } catch (CodeLightException e) {
            throw toOAuth2AuthenticationException(e);
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw toOAuth2AuthenticationException(
                new CodeLightException(ExceptionCodeType.OAUTH_USER_INFO_REQUEST_FAILED));
        }
    }

    private UserResult saveOrUpdate(OAuth2Attributes attributes) {
        OAuthProvider provider = OAuthProvider.valueOf(attributes.getProvider().toUpperCase());

        return oauthAccountRepository.findByProviderAndProviderUserId(provider, attributes.getProviderUserId())
            .map(oauthAccount -> {
                updateOAuthAccount(oauthAccount, attributes);
                User user = handleExistingUser(oauthAccount.getUser());
                return new UserResult(user, false);
            })
            .orElseGet(() -> {
                User newUser = createNewOAuthUser(attributes, provider);
                return new UserResult(newUser, true);
            });
    }

    private User handleExistingUser(User user) {
        if (user.isDeleted()) {
            authRepository.restoreUserByEmailAndLoginType(user.getEmail(), LoginType.OAUTH);
            return authRepository.findUserIncludingDeletedByEmailAndLoginType(user.getEmail(), LoginType.OAUTH)
                .orElseThrow(() -> new CodeLightException(ExceptionCodeType.USER_NOT_FOUND));
        }
        return user;
    }

    private User createNewOAuthUser(OAuth2Attributes attributes, OAuthProvider provider) {
        User newUser = User.builder()
            .email(attributes.getEmail())
            .name(attributes.getName())
            .password(null)
            .userRole(UserRole.USER)
            .loginType(LoginType.OAUTH)
            .build();

        User savedUser = authRepository.save(newUser);

        OAuthAccount oauthAccount = OAuthAccount.builder()
            .user(savedUser)
            .provider(provider)
            .providerUserId(attributes.getProviderUserId())
            .providerEmail(attributes.getEmail())
            .providerName(attributes.getName())
            .build();

        oauthAccountRepository.save(oauthAccount);

        return savedUser;
    }

    private void updateOAuthAccount(OAuthAccount oauthAccount, OAuth2Attributes attributes) {
        oauthAccount.updateProviderInfo(attributes.getEmail(), attributes.getName());
        oauthAccountRepository.save(oauthAccount);
    }

    private OAuth2AuthenticationException toOAuth2AuthenticationException(CodeLightException e) {
        ExceptionCodeType codeType = e.getExceptionCodeType();
        OAuth2Error error = new OAuth2Error(
            codeType.getExceptionCode().name(),
            codeType.getMessage(),
            null
        );
        return new OAuth2AuthenticationException(error, e);
    }

    private record UserResult(User user, boolean isNewUser) {}
}
