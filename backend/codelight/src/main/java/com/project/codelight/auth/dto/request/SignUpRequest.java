package com.project.codelight.auth.dto.request;

import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import com.project.codelight.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(

    @NotBlank(message = "이름이 존재하지 않습니다.")
    String name,

    @NotBlank(message = "이메일이 존재하지 않습니다.")
    String email,

    @NotBlank(message = "비밀번호가 존재하지 않습니다.")
    String password

) {

    public User toUserEntity() {
        return User.builder()
                   .name(name)
                   .email(email)
                   .password(password)
                   .userRole(UserRole.USER)
                   .loginType(LoginType.LOCAL)
                   .build();
    }
}