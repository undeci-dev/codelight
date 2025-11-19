import { useAppMutation } from '@/hooks/useAppMutation';
import { logOut } from '@/apis/user';

const useLogOutMutation = () => {
  return useAppMutation({
    mutationKey: ['auth', 'log-out'],
    mutationFn: logOut,
    successMessage: '로그아웃에 성공했습니다.',
    errorMessage: '로그아웃 과정에 문제가 생겼습니다.',
  });
};

export default useLogOutMutation;
