import { useAppMutation } from '@/hooks/useAppMutation';
import { signUp, signIn, kakaoLogin, logOut } from '@/apis/auth';

export const useSignUpMutation = () => {
  return useAppMutation({
    mutationKey: ['auth', 'sign-up'],
    mutationFn: signUp,
    successMessage: '회원가입이 완료되었습니다.',
    errorMessage: '회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.',
  });
};

export const useSignInMutation = () => {
  return useAppMutation({
    mutationKey: ['auth', 'sign-in'],
    mutationFn: signIn,
    successMessage: '',
    errorMessage: '로그인에 실패했습니다. 잠시 후 다시 시도해주세요.',
  });
};

export const useKakaoOAuthMutation = () => {
  return useAppMutation({
    mutationKey: ['auth', 'kakao-oauth'],
    mutationFn: kakaoLogin,
    successMessage: '',
    errorMessage: 'Kakao 로그인에 실패했습니다. 잠시 후 다시 시도해주세요.',
  });
};

export const useLogOutMutation = () => {
  return useAppMutation({
    mutationKey: ['auth', 'log-out'],
    mutationFn: logOut,
    successMessage: '로그아웃에 성공했습니다.',
    errorMessage: '로그아웃 과정에 문제가 생겼습니다.',
  });
};
