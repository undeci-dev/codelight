package com.project.codelight.global.exception;

import org.springframework.http.HttpStatusCode;

public class CodeLightException extends RuntimeException {

    private final ExceptionCodeType exceptionCodeType;

    public CodeLightException(ExceptionCodeType exceptionCodeType) {
        this.exceptionCodeType = exceptionCodeType;
    }

    @Override
    public String getMessage() {
        return exceptionCodeType.getMessage();
    }

    public HttpStatusCode getHttpStatusCode() {
        return exceptionCodeType.getHttpStatus();
    }

    public String getExceptionCodeName() {
        return exceptionCodeType.getExceptionCode().name();
    }
}