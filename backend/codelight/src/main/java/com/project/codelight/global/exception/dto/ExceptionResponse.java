package com.project.codelight.global.exception.dto;

public record ExceptionResponse(String httpMethod, String path, String codeLightCode,
                                String message) {

}