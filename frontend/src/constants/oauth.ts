import {
  VITE_KAKAO_AUTH_URL,
  VITE_KAKAO_CLIENT_ID,
  VITE_KAKAO_REDIRECT_URI,
} from '@/apis/config';

export const KAKAO_CONFIG = {
  AUTH_URL: VITE_KAKAO_AUTH_URL,
  CLIENT_ID: VITE_KAKAO_CLIENT_ID,
  REDIRECT_URI: VITE_KAKAO_REDIRECT_URI,
};

export const getKakaoAuthUrl = () => {
  const params = new URLSearchParams({
    client_id: KAKAO_CONFIG.CLIENT_ID,
    redirect_uri: KAKAO_CONFIG.REDIRECT_URI,
    response_type: 'code',
  });

  return `${KAKAO_CONFIG.AUTH_URL}?${params.toString()}`;
};
