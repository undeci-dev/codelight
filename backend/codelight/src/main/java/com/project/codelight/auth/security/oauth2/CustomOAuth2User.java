package com.project.codelight.auth.security.oauth2;

import com.project.codelight.user.domain.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final boolean isNewUser;

    public CustomOAuth2User(User user, Map<String, Object> attributes, String nameAttributeKey, boolean isNewUser) {
        this.user = user;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.isNewUser = isNewUser;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name())
        );
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get(nameAttributeKey));
    }
}
