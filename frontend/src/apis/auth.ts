import fetcher from '@/apis/fetcher';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';
import { SignInResponse } from '@/types/user';
import AppError from '@/core/error/AppError';
import useAuthStore from '@/store/useAuthStore';

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

  const accessToken = response.headers.get('Authorization');
  if (accessToken) {
    useAuthStore.getState().setAccessToken(accessToken);
  }

  return response;
};

export const logOut = async () => {
  try {
    const response = await fetcher.post({ url: BASE_URL + ENDPOINT.LOG_OUT });
    useAuthStore.getState().clearAccessToken();
    return response;
  } catch (err) {
    throw new AppError(500, 'INTERNAL_SERVER_ERROR');
  }
};
