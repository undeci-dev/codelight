import { VITE_API_URL } from '@/apis/config';

export const BASE_URL = VITE_API_URL;

export const ENDPOINT = {
  SIGN_UP: '/api/local-auth/register',
  SIGN_IN: '/api/local-auth/login',
  OAUTH_LOGIN: '/api/oauth/login',
  LOGOUT: '/api/logout',
  MYPAGE_INFO: '/api/mypage',
  ACCOUNT_DELETE: '/api/withdraw',
  NOTIFICATION: '/api/notification',
  FEED: '/api/feed',
  CODE_ON_AIR: '/api/code-on-air',
};
