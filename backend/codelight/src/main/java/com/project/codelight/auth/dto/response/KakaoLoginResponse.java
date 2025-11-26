package com.project.codelight.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoLoginResponse {

    private Long userId;
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;
    private boolean isNewUser;
}
