import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import Button from '@/components/_common/Button/Button';
import InputField from '@/components/_common/InputField/InputField';
import Text from '@/components/_common/Text/Text';
import { ROUTE_PATH } from '@/constants/routhPath';
import { useSignUpMutation } from '@/hooks/query/useAuthQuery';
import useValidateInput from '@/hooks/useValidateInput';
import {
  validateEmail,
  validateLength,
  validatePassword,
  validatePasswordConfirm,
} from '@/utils/authValidation';

const SignUpPage = () => {
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
    value: name,
    getErrorMessage: getNameError,
    onChange: onChangeName,
    isValidated: isNameValidated,
  } = useValidateInput({
    initialValue: '',
    validates: [(value: string) => validateLength(value, 2, 12)],
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

  const {
    value: passwordConfirm,
    getErrorMessage: getPasswordConfirmError,
    onChange: onChangePasswordConfirm,
    isValidated: isPasswordConfirmValidated,
  } = useValidateInput({
    initialValue: '',
    validates: [(value: string) => validatePasswordConfirm(value, password)],
  });

  const disabled =
    !isEmailValidated ||
    !isNameValidated ||
    !isPasswordValidated ||
    !isPasswordConfirmValidated;

  const { mutate } = useSignUpMutation();

  const handleSubmit = () => {
    mutate(
      { name, email, password },
      {
        onError: (error) => setErrorMessage(error.message),
        onSuccess: () => {
          setErrorMessage('');
          setTimeout(() => {
            navigate(ROUTE_PATH.signIn);
          }, 1500);
        },
      }
    );
  };

  const handleMoveSignIn = () => {
    navigate(ROUTE_PATH.signIn);
  };

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === 'Enter' && !disabled) {
      handleSubmit();
    }
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
            <InputField.Label label='닉네임' />
            <InputField.Input
              value={name}
              name='name'
              onChange={onChangeName}
              maxLength={12}
            />
            <InputField.ErrorMessage value={getNameError()} />
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
          <InputField>
            <InputField.Label label='비밀번호 확인' />
            <InputField.Input
              value={passwordConfirm}
              name='passwordConfirm'
              onChange={onChangePasswordConfirm}
              type='password'
              maxLength={12}
            />
            <InputField.ErrorMessage value={getPasswordConfirmError()} />
          </InputField>
          <InputField.ErrorMessage value={errorMessage} />
          <Button
            label='회원가입'
            variant='primary'
            fullWidth
            onClick={handleSubmit}
            disabled={disabled}
          />
        </div>
        <Text
          typography='body1'
          color='gray600'
          onClick={handleMoveSignIn}
          className='mt-8 cursor-pointer hover:text-green-600'
        >
          로그인
        </Text>
      </div>
    </>
  );
};

export default SignUpPage;
