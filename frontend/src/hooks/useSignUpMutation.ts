import { useAppMutation } from '@/hooks/useAppMutation';
import { signUp } from '@/apis/user';

const useSignUpMutation = () => {
  return useAppMutation({
    mutationKey: ['auth', 'sign-up'],
    mutationFn: signUp,
    successMessage: '회원가입이 완료되었습니다.',
    errorMessage: '회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.',
  });
};

export default useSignUpMutation;
