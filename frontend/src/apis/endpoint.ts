import { VITE_API_URL } from '@/apis/config';

export const BASE_URL = VITE_API_URL;

export const ENDPOINT = {
  SIGN_UP: '/api/local-auth/register',
  SIGN_IN: '/api/local-auth/login',
  OAUTH_LOGIN: '/oauth2/authorization/kakao',
  LOG_OUT: '/api/auth/logout',
  TOKEN_REFRESH: '/api/auth/token',
  MYPAGE_INFO: '/api/mypage',
  ACCOUNT_DELETE: '/api/withdraw',

  POSTS: '/api/posts',
  POST: '/api/post',
  POST_CREATE: '/api/post',
  POST_COMMENTS: '/api/post',

  COMMENT: '/api/comment',
  COMMENT_CREATE: '/api/post',
  COMMENT_LIKE: '/api/comment',
  COMMENT_REPLIES: '/api/comment',

  POLL: '/api/poll',
  POLL_BY_POST: '/api/post',
  POLL_VOTE: '/api/poll',

  LINK_PREVIEW: '/api/link/preview',

  CODE_ON_AIR: '/api/code-on-air',

  NOTIFICATION: '/api/notification',
};
