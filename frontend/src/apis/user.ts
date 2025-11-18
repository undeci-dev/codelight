import fetcher from '@/apis/fetcher';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';
import { SignInResponse } from '@/types/user';
import AppError from '@/core/error/AppError';

export const signUp = async ({
  name,
  email,
  password,
}: {
  name: string;
  email: string;
  password: string;
}) => {
  return await fetcher.post({
    url: BASE_URL + ENDPOINT.SIGN_UP,
    body: { name, email, password },
  });
};

export const signIn = async ({
  email,
  password,
}: {
  email: string;
  password: string;
}) => {
  const response = await fetcher.post({
    url: BASE_URL + ENDPOINT.SIGN_IN,
    body: { email, password },
  });

  const data: SignInResponse = await response.json();

  if (data.status !== 200)
    throw new AppError(data.status, 'USER_ACCOUNT_DELETED');

  localStorage.setItem('token', data.token);
  return response;
};
