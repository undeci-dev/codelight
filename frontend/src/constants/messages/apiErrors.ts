export const API_ERRORS: Record<string, string> = {
  AUTH_ACCESS_TOKEN_EMPTY:
    '로그인 후 이용 가능합니다.\n 로그인을 진행해 주세요.',
  AUTH_TOKEN_EMPTY: '로그인 후 이용 가능합니다.\n 로그인을 진행해 주세요.',
  AUTH_TOKEN_INVALID: '로그인 후 이용 가능합니다.\n 로그인을 진행해 주세요.',
  USER_ACCOUNT_DELETED:
    '탈퇴한 사용자입니다.\n 다른 아이디로 로그인을 진행해 주세요.',
  USER_NOT_FOUND: '사용자를 찾을 수 없어요.\n 다시 확인해 주세요.',
  USER_INVALID_FORMAT: '이메일 또는 비밀번호 형식이 올바르지 않아요.',
  USER_ACCOUNT_LOCKED:
    '여러 번 실패하여 계정이 잠겼어요. \n잠시 후 다시 시도해 주세요.',
  NETWORK_UNAVAILABLE:
    '네트워크에 연결할 수 없어요.\n 잠시 후 다시 시도해 주세요.',
};
