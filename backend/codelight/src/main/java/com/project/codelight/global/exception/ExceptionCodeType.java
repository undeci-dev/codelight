package com.project.codelight.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionCodeType {

    // ========== 전체/공통 ==========
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
        ExceptionCode.INTERNAL_SERVER_ERROR,
        "예상치 못한 서버에러가 발생했습니다"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, ExceptionCode.INVALID_PARAMETER, "잘못된 인자입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.RESOURCE_NOT_FOUND,
        "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, ExceptionCode.METHOD_NOT_ALLOWED,
        "허용되지 않은 HTTP 메서드입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, ExceptionCode.UNAUTHORIZED_ACCESS,
        "인증이 필요합니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, ExceptionCode.FORBIDDEN_ACCESS, "접근 권한이 없습니다."),

    // ========== 인증/인가 (AUTH) ==========
    // 회원가입
    USER_EMAIL_ALREADY_USED(HttpStatus.CONFLICT, ExceptionCode.USER_EMAIL_ALREADY_USED,
        "이미 해당 이메일을 사용하는 유저가 존재합니다."),
    USER_PASSWORD_POLICY_VIOLATION(HttpStatus.BAD_REQUEST,
        ExceptionCode.USER_PASSWORD_POLICY_VIOLATION,
        "비밀번호는 6자 이상, 영문/숫자/특수문자를 포함해야 합니다."),
    USER_EMAIL_INVALID_FORMAT(HttpStatus.BAD_REQUEST, ExceptionCode.USER_EMAIL_INVALID_FORMAT,
        "이메일 형식이 올바르지 않습니다."),

    // 로그인
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, ExceptionCode.USER_NOT_FOUND, "유저가 존재하지 않습니다."),
    USER_LOGIN_REQUEST_ERROR(HttpStatus.UNAUTHORIZED, ExceptionCode.USER_LOGIN_REQUEST_ERROR,
        "로그인 요청에 실패했습니다."),
    USER_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, ExceptionCode.USER_INVALID_CREDENTIALS,
        "사용자 정보가 올바르지 않습니다."),
    USER_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, ExceptionCode.USER_INVALID_PASSWORD,
        "비밀번호가 일치하지 않습니다."),
    USER_ACCOUNT_DELETED(HttpStatus.FORBIDDEN, ExceptionCode.USER_ACCOUNT_DELETED, "탈퇴한 계정입니다."),
    USER_ACCOUNT_BANNED(HttpStatus.FORBIDDEN, ExceptionCode.USER_ACCOUNT_BANNED, "정지된 계정입니다."),

    // 토큰
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, ExceptionCode.TOKEN_NOT_FOUND, "토큰이 존재하지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ExceptionCode.TOKEN_EXPIRED, "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, ExceptionCode.TOKEN_INVALID, "유효하지 않은 토큰입니다."),
    TOKEN_REFRESH_FAILED(HttpStatus.UNAUTHORIZED, ExceptionCode.TOKEN_REFRESH_FAILED,
        "토큰 갱신에 실패했습니다."),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, ExceptionCode.TOKEN_BLACKLISTED, "무효화된 토큰입니다."),

    // OAuth2
    OAUTH_PROVIDER_ERROR(HttpStatus.BAD_REQUEST, ExceptionCode.OAUTH_PROVIDER_ERROR,
        "OAuth 제공자 연동 중 오류가 발생했습니다."),
    OAUTH_INVALID_STATE(HttpStatus.BAD_REQUEST, ExceptionCode.OAUTH_INVALID_STATE,
        "유효하지 않은 OAuth 상태입니다."),
    OAUTH_CALLBACK_ERROR(HttpStatus.BAD_REQUEST, ExceptionCode.OAUTH_CALLBACK_ERROR,
        "OAuth 콜백 처리 중 오류가 발생했습니다."),
    OAUTH_TOKEN_REQUEST_FAILED(HttpStatus.BAD_REQUEST, ExceptionCode.OAUTH_TOKEN_REQUEST_FAILED,
        "OAuth 액세스 토큰 요청에 실패했습니다."),
    OAUTH_USER_INFO_REQUEST_FAILED(HttpStatus.BAD_REQUEST,
        ExceptionCode.OAUTH_USER_INFO_REQUEST_FAILED,
        "OAuth 사용자 정보 요청에 실패했습니다."),
    OAUTH_SERVER_ERROR(HttpStatus.BAD_GATEWAY, ExceptionCode.OAUTH_SERVER_ERROR,
        "OAuth 서버에서 오류가 발생했습니다."),
    OAUTH_EMAIL_NOT_PROVIDED(HttpStatus.BAD_REQUEST, ExceptionCode.OAUTH_EMAIL_NOT_PROVIDED,
        "OAuth 제공자로부터 이메일 정보를 받지 못했습니다."),

    // 권한
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, ExceptionCode.INSUFFICIENT_PERMISSION,
        "권한이 부족합니다."),
    ADMIN_ONLY(HttpStatus.FORBIDDEN, ExceptionCode.ADMIN_ONLY, "관리자만 접근 가능합니다."),
    PRESENTER_ONLY(HttpStatus.FORBIDDEN, ExceptionCode.PRESENTER_ONLY, "발표자만 접근 가능합니다."),

    // ========== 공지사항 (NOTICE) ==========
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.NOTICE_NOT_FOUND, "공지사항을 찾을 수 없습니다."),
    NOTICE_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.NOTICE_TITLE_REQUIRED,
        "공지사항 제목은 필수입니다."),
    NOTICE_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.NOTICE_CONTENT_REQUIRED,
        "공지사항 내용은 필수입니다."),
    NOTICE_XSS_DETECTED(HttpStatus.BAD_REQUEST, ExceptionCode.NOTICE_XSS_DETECTED,
        "허용되지 않는 스크립트가 포함되어 있습니다."),
    NOTICE_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, ExceptionCode.NOTICE_NOT_OWNED_BY_USER,
        "본인이 작성한 공지사항이 아닙니다."),
    NOTICE_ALREADY_DELETED(HttpStatus.BAD_REQUEST, ExceptionCode.NOTICE_ALREADY_DELETED,
        "이미 삭제된 공지사항입니다."),

    // ========== 마이페이지 (PROFILE) ==========
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.PROFILE_NOT_FOUND, "프로필을 찾을 수 없습니다."),
    PROFILE_NICKNAME_DUPLICATED(HttpStatus.CONFLICT, ExceptionCode.PROFILE_NICKNAME_DUPLICATED,
        "이미 사용 중인 닉네임입니다."),
    PROFILE_NICKNAME_INVALID(HttpStatus.BAD_REQUEST, ExceptionCode.PROFILE_NICKNAME_INVALID,
        "닉네임은 2-20자의 한글, 영문, 숫자만 가능합니다."),
    PROFILE_BIO_TOO_LONG(HttpStatus.BAD_REQUEST, ExceptionCode.PROFILE_BIO_TOO_LONG,
        "소개는 최대 200자까지 입력 가능합니다."),
    PROFILE_INVALID_URL(HttpStatus.BAD_REQUEST, ExceptionCode.PROFILE_INVALID_URL,
        "유효하지 않은 URL 형식입니다."),

    // 아바타/이미지
    IMAGE_FILE_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.IMAGE_FILE_REQUIRED,
        "이미지 파일이 필요합니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, ExceptionCode.IMAGE_SIZE_EXCEEDED,
        "이미지 크기는 최대 5MB까지 가능합니다."),
    IMAGE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, ExceptionCode.IMAGE_INVALID_FORMAT,
        "지원하지 않는 이미지 형식입니다. (jpg, png, gif만 가능)"),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionCode.IMAGE_UPLOAD_FAILED,
        "이미지 업로드에 실패했습니다."),
    S3_PRESIGNED_URL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionCode.S3_PRESIGNED_URL_ERROR,
        "Pre-signed URL 생성에 실패했습니다."),

    // ========== 피드 (POST) ==========
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.POST_NOT_FOUND, "피드를 찾을 수 없습니다."),
    POST_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.POST_CONTENT_REQUIRED,
        "피드 내용은 필수입니다."),
    POST_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, ExceptionCode.POST_CONTENT_TOO_LONG,
        "피드 내용은 최대 500자까지 입력 가능합니다."),
    POST_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, ExceptionCode.POST_NOT_OWNED_BY_USER,
        "본인이 작성한 피드가 아닙니다."),
    POST_ALREADY_DELETED(HttpStatus.BAD_REQUEST, ExceptionCode.POST_ALREADY_DELETED,
        "이미 삭제된 피드입니다."),
    POST_INVALID_CURSOR(HttpStatus.BAD_REQUEST, ExceptionCode.POST_INVALID_CURSOR,
        "유효하지 않은 커서입니다."),

    // 링크 프리뷰
    LINK_PREVIEW_FETCH_FAILED(HttpStatus.BAD_REQUEST, ExceptionCode.LINK_PREVIEW_FETCH_FAILED,
        "링크 프리뷰를 가져올 수 없습니다."),
    LINK_PREVIEW_INVALID_URL(HttpStatus.BAD_REQUEST, ExceptionCode.LINK_PREVIEW_INVALID_URL,
        "유효하지 않은 URL입니다."),
    LINK_PREVIEW_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, ExceptionCode.LINK_PREVIEW_TIMEOUT,
        "링크 프리뷰 요청 시간이 초과되었습니다."),

    // 파일 첨부
    POST_FILE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, ExceptionCode.POST_FILE_COUNT_EXCEEDED,
        "첨부 파일은 최대 4개까지 가능합니다."),
    GIF_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, ExceptionCode.GIF_SIZE_EXCEEDED,
        "GIF 파일은 최대 10MB까지 가능합니다."),
    GIF_FRAME_EXCEEDED(HttpStatus.BAD_REQUEST, ExceptionCode.GIF_FRAME_EXCEEDED,
        "GIF 프레임 수가 너무 많습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionCode.FILE_UPLOAD_FAILED,
        "파일 업로드에 실패했습니다."),

    // 리액션/상호작용
    EMOJI_INVALID(HttpStatus.BAD_REQUEST, ExceptionCode.EMOJI_INVALID, "유효하지 않은 이모지입니다."),
    LIKE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ExceptionCode.LIKE_ALREADY_EXISTS,
        "이미 좋아요를 누른 피드입니다."),
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.LIKE_NOT_FOUND, "좋아요를 찾을 수 없습니다."),

    // 댓글
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.COMMENT_NOT_FOUND, "댓글을 찾을 수 없습니다."),
    COMMENT_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.COMMENT_CONTENT_REQUIRED,
        "댓글 내용은 필수입니다."),
    COMMENT_CONTENT_TOO_LONG(HttpStatus.BAD_REQUEST, ExceptionCode.COMMENT_CONTENT_TOO_LONG,
        "댓글은 최대 200자까지 입력 가능합니다."),
    COMMENT_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN, ExceptionCode.COMMENT_NOT_OWNED_BY_USER,
        "본인이 작성한 댓글이 아닙니다."),
    COMMENT_ALREADY_REPORTED(HttpStatus.BAD_REQUEST, ExceptionCode.COMMENT_ALREADY_REPORTED,
        "이미 신고한 댓글입니다."),

    // ========== 투표 (POLL) ==========
    POLL_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.POLL_NOT_FOUND, "투표를 찾을 수 없습니다."),
    POLL_OPTIONS_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.POLL_OPTIONS_REQUIRED,
        "투표 옵션은 최소 2개 이상이어야 합니다."),
    POLL_OPTIONS_EXCEEDED(HttpStatus.BAD_REQUEST, ExceptionCode.POLL_OPTIONS_EXCEEDED,
        "투표 옵션은 최대 4개까지 가능합니다."),
    POLL_OPTION_DUPLICATED(HttpStatus.BAD_REQUEST, ExceptionCode.POLL_OPTION_DUPLICATED,
        "중복된 투표 옵션이 존재합니다."),
    POLL_MULTIPLE_VOTE_NOT_ALLOWED(HttpStatus.BAD_REQUEST,
        ExceptionCode.POLL_MULTIPLE_VOTE_NOT_ALLOWED,
        "단일 선택 투표에서는 하나의 옵션만 선택 가능합니다."),
    POLL_ALREADY_VOTED(HttpStatus.BAD_REQUEST, ExceptionCode.POLL_ALREADY_VOTED,
        "이미 투표가 진행된 투표는 수정할 수 없습니다."),
    POLL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, ExceptionCode.POLL_ALREADY_EXISTS,
        "이미 투표가 존재합니다."),
    POLL_VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.POLL_VOTE_NOT_FOUND,
        "투표 기록을 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN, ExceptionCode.UNAUTHORIZED,
        "해당 리소스에 대한 권한이 없습니다."),

    // ========== 발표 (PRESENTATION) ==========
    // 일정
    PRESENTATION_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.PRESENTATION_NOT_FOUND,
        "발표 일정을 찾을 수 없습니다."),
    PRESENTATION_TITLE_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.PRESENTATION_TITLE_REQUIRED,
        "발표 제목은 필수입니다."),
    PRESENTATION_INVALID_DURATION(HttpStatus.BAD_REQUEST,
        ExceptionCode.PRESENTATION_INVALID_DURATION,
        "발표 시간은 최소 30분, 최대 2시간입니다."),
    PRESENTATION_TIME_CONFLICT(HttpStatus.CONFLICT, ExceptionCode.PRESENTATION_TIME_CONFLICT,
        "해당 시간에 이미 다른 발표가 예정되어 있습니다."),
    PRESENTATION_PAST_TIME(HttpStatus.BAD_REQUEST, ExceptionCode.PRESENTATION_PAST_TIME,
        "과거 시간으로 발표를 예약할 수 없습니다."),
    PRESENTATION_NOT_OWNED_BY_USER(HttpStatus.FORBIDDEN,
        ExceptionCode.PRESENTATION_NOT_OWNED_BY_USER,
        "본인이 등록한 발표가 아닙니다."),
    PRESENTATION_ALREADY_STARTED(HttpStatus.BAD_REQUEST, ExceptionCode.PRESENTATION_ALREADY_STARTED,
        "이미 시작된 발표는 수정할 수 없습니다."),

    // 슬라이드
    SLIDE_FILE_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.SLIDE_FILE_REQUIRED,
        "슬라이드 파일이 필요합니다."),
    SLIDE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, ExceptionCode.SLIDE_INVALID_FORMAT,
        "지원하지 않는 파일 형식입니다. (PPT, PPTX, PDF만 가능)"),
    SLIDE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, ExceptionCode.SLIDE_SIZE_EXCEEDED,
        "슬라이드 파일은 최대 50MB까지 가능합니다."),
    SLIDE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionCode.SLIDE_UPLOAD_FAILED,
        "슬라이드 업로드에 실패했습니다."),
    SLIDE_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.SLIDE_NOT_FOUND,
        "슬라이드를 찾을 수 없습니다."),

    // 방/세션
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.ROOM_NOT_FOUND,
        "발표 방을 찾을 수 없습니다."),
    ROOM_ALREADY_FULL(HttpStatus.BAD_REQUEST, ExceptionCode.ROOM_ALREADY_FULL,
        "발표 방 인원이 가득 찼습니다."),
    ROOM_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, ExceptionCode.ROOM_ALREADY_CLOSED,
        "이미 종료된 발표입니다."),
    ROOM_NOT_STARTED(HttpStatus.BAD_REQUEST, ExceptionCode.ROOM_NOT_STARTED,
        "아직 시작되지 않은 발표입니다."),
    ROOM_ACCESS_DENIED(HttpStatus.FORBIDDEN, ExceptionCode.ROOM_ACCESS_DENIED,
        "발표 방 접근 권한이 없습니다."),
    ROOM_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, ExceptionCode.ROOM_TOKEN_INVALID,
        "유효하지 않은 세션 토큰입니다."),
    ROOM_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ExceptionCode.ROOM_TOKEN_EXPIRED,
        "세션 토큰이 만료되었습니다."),
    ROOM_USER_KICKED(HttpStatus.FORBIDDEN, ExceptionCode.ROOM_USER_KICKED,
        "강퇴된 사용자는 재입장할 수 없습니다."),
    ROOM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.ROOM_USER_NOT_FOUND,
        "방에 존재하지 않는 사용자입니다."),

    // WebRTC
    WEBRTC_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
        ExceptionCode.WEBRTC_CONNECTION_FAILED, "음성 연결에 실패했습니다."),
    WEBRTC_PEER_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.WEBRTC_PEER_NOT_FOUND,
        "연결된 피어를 찾을 수 없습니다."),

    // 채팅
    CHAT_MESSAGE_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.CHAT_MESSAGE_REQUIRED,
        "메시지 내용은 필수입니다."),
    CHAT_MESSAGE_TOO_LONG(HttpStatus.BAD_REQUEST, ExceptionCode.CHAT_MESSAGE_TOO_LONG,
        "메시지는 최대 100자까지 입력 가능합니다."),
    CHAT_RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, ExceptionCode.CHAT_RATE_LIMIT_EXCEEDED,
        "메시지 전송 속도 제한을 초과했습니다."),

    // ========== 관리자 (ADMIN) ==========
    ADMIN_USER_SEARCH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
        ExceptionCode.ADMIN_USER_SEARCH_FAILED,
        "사용자 검색에 실패했습니다."),
    ADMIN_CANNOT_BAN_ADMIN(HttpStatus.BAD_REQUEST, ExceptionCode.ADMIN_CANNOT_BAN_ADMIN,
        "관리자는 다른 관리자를 정지시킬 수 없습니다."),
    ADMIN_CANNOT_MODIFY_SELF(HttpStatus.BAD_REQUEST, ExceptionCode.ADMIN_CANNOT_MODIFY_SELF,
        "자신의 권한을 변경할 수 없습니다."),
    ADMIN_INVALID_ROLE(HttpStatus.BAD_REQUEST, ExceptionCode.ADMIN_INVALID_ROLE, "유효하지 않은 권한입니다."),
    ADMIN_BAN_REASON_REQUIRED(HttpStatus.BAD_REQUEST, ExceptionCode.ADMIN_BAN_REASON_REQUIRED,
        "정지 사유는 필수입니다."),
    AUDIT_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, ExceptionCode.AUDIT_LOG_NOT_FOUND,
        "감사 로그를 찾을 수 없습니다."),

    // ========== 카테고리 (CATEGORY) ==========
    CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, ExceptionCode.CATEGORY_NOT_FOUND,
        "카테고리 ID가 존재하지 않습니다"),
    CATEGORY_DUPLICATED(HttpStatus.BAD_REQUEST, ExceptionCode.CATEGORY_DUPLICATED,
        "중복된 카테고리가 존재합니다."),
    CATEGORY_HAS_ITEMS(HttpStatus.BAD_REQUEST, ExceptionCode.CATEGORY_HAS_ITEMS,
        "해당 카테고리에 속한 항목이 존재하여 삭제할 수 없습니다."),

    // ========== 캐시 (CACHE) ==========
    CACHE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionCode.CACHE_CONNECTION_ERROR,
        "캐시 서버 연결에 실패했습니다."),
    CACHE_OPERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionCode.CACHE_OPERATION_FAILED,
        "캐시 작업에 실패했습니다."),

    // ========== 외부 서비스 (EXTERNAL) ==========
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, ExceptionCode.EXTERNAL_API_ERROR,
        "외부 API 호출에 실패했습니다."),
    EXTERNAL_API_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, ExceptionCode.EXTERNAL_API_TIMEOUT,
        "외부 API 응답 시간이 초과되었습니다."),

    // ========== 게스트 (GUEST) ==========
    GUEST_USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionCode.GUEST_USER_NOT_FOUND,
        "게스트 유저가 존재하지 않습니다."),
    GUEST_USER_UNEXPECTED_EXIST(HttpStatus.INTERNAL_SERVER_ERROR,
        ExceptionCode.GUEST_USER_UNEXPECTED_EXIST,
        "예상치 못한 게스트 유저가 존재합니다. 데이터베이스를 확인해주세요.");

    private final HttpStatus httpStatus;
    private final ExceptionCode exceptionCode;
    private final String message;

    ExceptionCodeType(HttpStatus httpStatus, ExceptionCode exceptionCode, String message) {
        this.httpStatus = httpStatus;
        this.exceptionCode = exceptionCode;
        this.message = message;
    }
}
