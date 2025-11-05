import { VITE_API_URL } from '@/apis/config';

export const BASE_URL = VITE_API_URL;

export const ENDPOINT = {
  SIGN_UP: '/local-auth/register',
  SIGN_IN: '/local-auth/login',
  OAUTH_LOGIN: '/oauth/login',
  LOGOUT: '/logout',
  MYPAGE_INFO: '/mypage',
  ACCOUNT_DELETE: '/withdraw',
  NOTIFICATION: '/notification',
  FEED: '/feed',
  CODE_ON_AIR: '/code-on-air',
};
