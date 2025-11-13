package com.project.codelight.user.dto;

import com.project.codelight.user.domain.LoginType;
import com.project.codelight.user.domain.User;
import com.project.codelight.user.domain.UserRole;
import jakarta.validation.constraints.NotBlank;

public class SignUpRequest {

    @NotBlank(message = "이름이 존재하지 않습니다.")
    private final String name;

    @NotBlank(message = "이메일이 존재하지 않습니다.")
    private final String email;

    @NotBlank(message = "비밀번호가 존재하지 않습니다.")
    private final String password;

    public SignUpRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public User toUserEntity() {
        return new User(name, email, password, UserRole.USER, LoginType.LOCAL);
    }
}