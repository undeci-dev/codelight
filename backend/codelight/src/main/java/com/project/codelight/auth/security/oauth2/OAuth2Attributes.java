package com.project.codelight.auth.security.oauth2;

import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.ExceptionCodeType;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2Attributes {

    private String providerUserId;
    private String email;
    private String name;
    private String provider;
    private Map<String, Object> attributes;
    private String nameAttributeKey;

    public static OAuth2Attributes of(String registrationId, String userNameAttributeName,
                                      Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        throw new CodeLightException(ExceptionCodeType.OAUTH_PROVIDER_ERROR);
    }

    @SuppressWarnings("unchecked")
    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");
        String providerUserId = String.valueOf(attributes.get("id"));

        return OAuth2Attributes.builder()
            .providerUserId(providerUserId)
            .email(email)
            .name(nickname != null ? nickname : (email != null ? email.split("@")[0] : "User"))
            .provider("kakao")
            .attributes(attributes)
            .nameAttributeKey(userNameAttributeName)
            .build();
    }
}
