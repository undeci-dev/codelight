package com.project.codelight.global.exception;

public record ExceptionResponse(String httpMethod, String path, String codeLightCode,
                                String message) {

}