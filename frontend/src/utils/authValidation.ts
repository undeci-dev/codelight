import { ValidationResult } from '@/hooks/useValidateInput';

const REGEX = {
  EMAIL: /^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  PASSWORD: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d\W]{6,}$/,
};

export const validateEmail = (value: string): ValidationResult => {
  const regex = REGEX.EMAIL;
  if (!regex.test(value)) {
    return { errorType: 'ILLEGAL_EMAIL_FORMAT', isValid: false };
  }
  return { errorType: 'ILLEGAL_EMAIL_FORMAT', isValid: true };
};

export const validatePassword = (value: string): ValidationResult => {
  const regex = REGEX.PASSWORD;
  if (!regex.test(value)) {
    return { errorType: 'ILLEGAL_PASSWORD_FORMAT', isValid: false };
  }
  return { errorType: 'ILLEGAL_PASSWORD_FORMAT', isValid: true };
};

export const validatePasswordConfirm = (
  value: string,
  password: string
): ValidationResult => {
  if (value !== password) {
    return { errorType: 'ILLEGAL_CONFIRM_PASSWORD', isValid: false };
  }
  return { errorType: 'ILLEGAL_CONFIRM_PASSWORD', isValid: true };
};

export const validateLength = (
  value: string,
  minLength: number,
  maxLength: number
): ValidationResult => {
  if (value.length < minLength || value.length > maxLength) {
    return { errorType: 'ILLEGAL_NAME_LENGTH', isValid: false };
  }
  return { errorType: 'ILLEGAL_NAME_LENGTH', isValid: true };
};
