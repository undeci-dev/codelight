import { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import useKakaoOAuthMutation from '@/hooks/useKakaoOAuthMutation';
import { ROUTE_PATH } from '@/constants/routhPath';
import Text from '@/components/_common/Text/Text';

const KakaoCallbackPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { mutate } = useKakaoOAuthMutation();

  useEffect(() => {
    const code = searchParams.get('code');
    const error = searchParams.get('error');

    if (error) {
      console.error('Kakao OAuth error:', error);
      navigate(ROUTE_PATH.signIn, { replace: true });
      return;
    }

    if (!code) {
      console.error('No authorization code received');
      navigate(ROUTE_PATH.signIn, { replace: true });
      return;
    }

    mutate(
      { code },
      {
        onSuccess: () => {
          setTimeout(() => {
            navigate(ROUTE_PATH.main, { replace: true });
          }, 1500);
        },
        onError: (error) => {
          console.error('Kakao login error:', error);
          setTimeout(() => {
            navigate(ROUTE_PATH.signIn, { replace: true });
          }, 2000);
        },
      }
    );
  }, [searchParams, navigate, mutate]);

  return (
    <div className='flex h-[calc(100dvh)] items-center justify-center'>
      <div className='flex flex-col items-center gap-4'>
        <div className='h-12 w-12 animate-spin rounded-full border-4 border-gray-300 border-t-green-600' />
        <Text typography='h3' color='gray900'>
          Kakao 로그인 처리중...
        </Text>
      </div>
    </div>
  );
};

export default KakaoCallbackPage;
