package com.project.codelight.global.handler;

import com.project.codelight.global.exception.CodeLightException;
import com.project.codelight.global.exception.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CodeLightException.class)
    public ResponseEntity<ExceptionResponse> handleCodeLightException(CodeLightException exception,
                                                                      HttpServletRequest request) {
        ExceptionResponse response = new ExceptionResponse(
            request.getMethod(),
            request.getRequestURI(),
            exception.getExceptionCodeName(),
            exception.getMessage());

        return ResponseEntity.status(exception.getHttpStatusCode())
                             .body(response);
    }
}
