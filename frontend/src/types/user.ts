export interface TokenValidity {
  hasAccessToken: boolean;
  hasRefreshToken: boolean;
}

export interface ResetPasswordRequest {
  email: string;
  code: string;
  newPassword: string;
}

export interface SignInRequest {
  email: string;
  password: string;
}

export interface SignInResponse {
  status: number;
  accessToken: string;
  refreshToken?: string; // HttpOnly Cookie로 전달되므로 optional
  codeLightCode: string;
}

export interface ExceptionResponse {
  httpMethod?: string;
  path?: string;
  codeLightCode: string | number;
  message: string;
}

export interface KakaoLoginRequest {
  code: string;
}

export interface KakaoLoginResponse {
  userId: number;
  email: string;
  name?: string;
  accessToken: string;
  refreshToken: string;
  isNewUser: boolean;
}
