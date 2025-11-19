import { useNavigate } from 'react-router-dom';
import { ROUTE_PATH } from '@/constants/routhPath';
import Button from '@/components/_common/Button/Button';
import Text from '@/components/_common/Text/Text';
import useLogOutMutation from '@/hooks/useLogOutMutation';

const MainPage = () => {
  const navigate = useNavigate();

  const logIn = () => {
    navigate(ROUTE_PATH.signIn);
  };

  const { mutate } = useLogOutMutation();

  const handleSubmit = () => {
    mutate(undefined, {
      onSuccess: () => {
        setTimeout(() => {
          navigate(ROUTE_PATH.main);
        }, 1500);
      },
    });
  };

  return (
    <>
      <div className='flex h-[calc(100dvh-56px)] flex-col items-center justify-center gap-4'>
        <Text
          typography='body1'
          color='gray600'
          className='mt-8 cursor-pointer hover:text-green-600'
        >
          MainPage
        </Text>
        <Button label='로그인' variant='primary' fullWidth onClick={logIn} />
        <Button
          label='로그아웃'
          variant='primary'
          fullWidth
          onClick={handleSubmit}
        />
      </div>
    </>
  );
};

export default MainPage;
