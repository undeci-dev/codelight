import { useAppMutation } from '@/hooks/useAppMutation';
import { kakaoLogin } from '@/apis/user';

const useKakaoOAuthMutation = () => {
  return useAppMutation({
    mutationKey: ['auth', 'kakao-oauth'],
    mutationFn: kakaoLogin,
    successMessage: '',
    errorMessage: 'Kakao 로그인에 실패했습니다. 잠시 후 다시 시도해주세요.',
  });
};

export default useKakaoOAuthMutation;
