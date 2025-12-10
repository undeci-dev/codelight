import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { ROUTE_PATH } from '@/constants/routhPath';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';
import useAuthStore from '@/store/useAuthStore';
import Text from '@/components/_common/Text/Text';

const KakaoCallbackPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const error = searchParams.get('error');

    if (error) {
      console.error('Kakao OAuth error:', error);
      navigate(ROUTE_PATH.signIn, { replace: true });
      return;
    }

    const fetchAccessToken = async () => {
      try {
        const response = await fetch(BASE_URL + ENDPOINT.TOKEN_REFRESH, {
          method: 'POST',
          credentials: 'include',
        });

        if (response.ok) {
          const accessToken = response.headers.get('Authorization');
          if (accessToken) {
            useAuthStore.getState().setAccessToken(accessToken);
          }
          navigate(ROUTE_PATH.main, {
            replace: true,
          });
        } else {
          console.error('Failed to fetch access token');
          navigate(ROUTE_PATH.signIn, { replace: true });
        }
      } catch (err) {
        console.error('Token fetch error:', err);
        navigate(ROUTE_PATH.signIn, { replace: true });
      }
    };

    fetchAccessToken();
  }, [searchParams, navigate]);

  return (
    <div className='flex h-[calc(100dvh)] items-center justify-center'>
      <div className='flex flex-col items-center gap-4'>
        <div className='h-12 w-12 animate-spin rounded-full border-4 border-gray-300 border-t-purple-600' />
        <Text typography='h3' color='gray900'>
          로그인 처리중...
        </Text>
      </div>
    </div>
  );
};

export default KakaoCallbackPage;
