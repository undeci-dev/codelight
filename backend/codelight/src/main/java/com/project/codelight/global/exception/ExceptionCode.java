package com.project.codelight.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {
    // 공통
    INTERNAL_SERVER_ERROR,
    INVALID_PARAMETER,
    RESOURCE_NOT_FOUND,
    METHOD_NOT_ALLOWED,
    UNAUTHORIZED_ACCESS,
    FORBIDDEN_ACCESS,

    // 인증/인가
    AUTH_ERROR,
    LOGIN_ERROR,
    TOKEN_ERROR,
    PERMISSION_ERROR,
    USER_NOT_FOUND,
    USER_EMAIL_ALREADY_USED,

    // 공지사항
    NOTICE_ERROR,

    // 프로필
    PROFILE_ERROR,
    FILE_ERROR,

    // 피드
    FEED_ERROR,

    // 투표
    POLL_ERROR,

    // 발표
    PRESENTATION_ERROR,

    // 관리자
    ADMIN_ERROR,

    // 카테고리
    CATEGORY_ERROR,

    // 캐시
    CACHE_ERROR,

    // 외부 서비스
    EXTERNAL_ERROR,

    // 게스트
    GUEST_USER_NOT_FOUND,
    GUEST_USER_UNEXPECTED_EXIST,
}