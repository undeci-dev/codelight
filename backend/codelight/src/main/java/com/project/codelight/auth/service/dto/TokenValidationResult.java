package com.project.codelight.auth.service.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenValidationResult {

    private boolean isValid = false;
    private String exceptionCodeTypeName = "";

    @Builder(toBuilder = true)
    public TokenValidationResult(boolean isValid, String exceptionCodeTypeName) {
        this.isValid = isValid;
        this.exceptionCodeTypeName = exceptionCodeTypeName;
    }
}