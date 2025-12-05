import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Button from '@/components/_common/Button/Button';
import InputField from '@/components/_common/InputField/InputField';
import Text from '@/components/_common/Text/Text';
import { ROUTE_PATH } from '@/constants/routhPath';
import { useSignInMutation } from '@/hooks/query/useAuthQuery';
import useValidateInput from '@/hooks/useValidateInput';
import { validateEmail, validatePassword } from '@/utils/authValidation';
import { getKakaoAuthUrl } from '@/constants/oauth';

const SignInPage = () => {
  const [errorMessage, setErrorMessage] = useState('');
  const navigate = useNavigate();

  const {
    value: email,
    getErrorMessage: getEmailError,
    onChange: onChangeEmail,
    isValidated: isEmailValidated,
  } = useValidateInput({
    initialValue: '',
    validates: [validateEmail],
  });

  const {
    value: password,
    getErrorMessage: getPasswordError,
    onChange: onChangePassword,
    isValidated: isPasswordValidated,
  } = useValidateInput({
    initialValue: '',
    validates: [validatePassword],
  });

  const disabled = !isEmailValidated || !isPasswordValidated;

  const { mutate } = useSignInMutation();

  const handleSignIn = () => {
    mutate(
      { email, password },
      {
        onError: (error) => setErrorMessage(error.message),
        onSuccess: () => {
          setErrorMessage('');
          setTimeout(() => {
            navigate(ROUTE_PATH.main);
          }, 1500);
        },
      }
    );
  };

  const handleMoveSignUp = () => {
    navigate(ROUTE_PATH.signUp);
  };

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' && !disabled) {
      handleSignIn();
    }
  };

  const handleKakaoLogin = () => {
    window.location.href = getKakaoAuthUrl();
  };

  return (
    <>
      <div className='flex h-[calc(100dvh-56px)] flex-col items-center justify-center gap-4'>
        <div className='relative flex w-[36.1rem] flex-col gap-0 rounded-2xl px-6'>
          <InputField onKeyDown={handleKeyDown}>
            <InputField.Label label='이메일' />
            <InputField.Input
              value={email}
              name='email'
              onChange={onChangeEmail}
              maxLength={254}
            />
            <InputField.ErrorMessage value={getEmailError()} />
          </InputField>
          <InputField>
            <InputField.Label label='비밀번호' />
            <InputField.Input
              value={password}
              name='password'
              onChange={onChangePassword}
              type='password'
              maxLength={12}
            />
            <InputField.ErrorMessage value={getPasswordError()} />
          </InputField>
          <InputField.ErrorMessage value={errorMessage} />
          <Button
            label='로그인'
            variant='primary'
            fullWidth
            onClick={handleSignIn}
            disabled={disabled}
          />
          <div className='my-4 flex items-center gap-2'>
            <div className='h-px flex-1 bg-gray-300' />
            <Text typography='body2' color='gray600'>
              또는
            </Text>
            <div className='h-px flex-1 bg-gray-300' />
          </div>
          <button
            onClick={handleKakaoLogin}
            className='flex w-full items-center justify-center gap-2 rounded-lg bg-[#FEE500] px-4 py-3 transition-opacity hover:opacity-90'
          >
            <Text typography='body1' color='gray900' className='font-semibold'>
              Kakao 로그인
            </Text>
          </button>
        </div>
        <Text
          typography='body1'
          color='gray600'
          onClick={handleMoveSignUp}
          className='mt-8 cursor-pointer hover:text-green-600'
        >
          회원가입
        </Text>
      </div>
    </>
  );
};

export default SignInPage;
