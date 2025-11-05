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
