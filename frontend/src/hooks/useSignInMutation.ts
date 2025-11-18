import { useAppMutation } from '@/hooks/useAppMutation';
import { signIn } from '@/apis/user';

const useSignInMutation = () => {
  return useAppMutation({
    mutationKey: ['auth', 'sign-in'],
    mutationFn: signIn,
    successMessage: '',
    errorMessage: '로그인에 실패했습니다. 잠시 후 다시 시도해주세요.',
  });
};

export default useSignInMutation;
