import { API_ERRORS } from '@/constants/messages/apiErrors';

class AppError extends Error {
  statusCode: number;
  errorCode: string;
  errorMessage: string;

  constructor(statusCode: number, errorCode: string) {
    super();

    const getErrorMessage = (errorCode: string): string => {
      return (
        API_ERRORS[errorCode as keyof typeof API_ERRORS] ??
        '일시적인 문제가 발생했어요.\n 잠시 후 다시 시도해 주세요'
      );
    };

    Object.setPrototypeOf(this, AppError.prototype);

    this.statusCode = statusCode;
    this.errorCode = errorCode;
    this.errorMessage = getErrorMessage(errorCode);
  }
}

export default AppError;
