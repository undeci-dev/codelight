export const VALIDATION_ERRORS = {
  ILLEGAL_EMAIL_FORMAT: '올바른 이메일 형식을 입력해주세요.',
  ILLEGAL_PASSWORD_FORMAT:
    '비밀번호는 영어, 숫자를 포함해 6~12자 이내로 입력해주세요.',
  ILLEGAL_CONFIRM_PASSWORD: '비밀번호 확인이 일치하지 않습니다.',
  ILLEGAL_NAME_LENGTH: '닉네임은 2~12자 이내로 입력해주세요.',
};

export type ValidationError = keyof typeof VALIDATION_ERRORS;
