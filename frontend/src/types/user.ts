export type UserType = 'ADMIN' | 'USER' | 'GUEST';

export interface User {
  userId: number;
  userName: string;
  userEmail: string;
  userType: UserType;
  createdAt: string;
}

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
