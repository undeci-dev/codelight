import { useEffect, useState } from 'react';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';
import useAuthStore from '@/store/useAuthStore';

/**
 * 앱 초기화 시 refresh token으로 access token을 재발급받는 훅
 * 페이지 새로고침/재접속 시에도 로그인 상태를 유지하기 위함
 */
const useInitAuth = () => {
  const [isInitialized, setIsInitialized] = useState(false);
  const setAccessToken = useAuthStore((state) => state.setAccessToken);
  const getAccessToken = useAuthStore((state) => state.getAccessToken);

  useEffect(() => {
    const initAuth = async () => {
      // 이미 accessToken이 있으면 초기화 완료
      if (getAccessToken()) {
        setIsInitialized(true);
        return;
      }

      try {
        const response = await fetch(BASE_URL + ENDPOINT.TOKEN_REFRESH, {
          method: 'POST',
          credentials: 'include', // RefreshToken 쿠키 전송
        });

        if (response.ok) {
          const accessToken = response.headers.get('Authorization');
          if (accessToken) {
            setAccessToken(accessToken);
          }
        }
      } catch {
        // 토큰 갱신 실패 시 로그인되지 않은 상태로 진행
      } finally {
        setIsInitialized(true);
      }
    };

    initAuth();
  }, []);

  return { isInitialized };
};

export default useInitAuth;
