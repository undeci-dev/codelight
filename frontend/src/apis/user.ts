import fetcher from '@/apis/fetcher';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';

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
