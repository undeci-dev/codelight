import AppError from '@/core/error/AppError';
import { API_ERRORS } from '@/constants/messages/apiErrors';
import useAuthStore from '@/store/useAuthStore';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';

interface HttpRequestOptions {
  url: string;
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: object;
  headers?: HeadersInit;
  params?: Record<string, string | string[] | number>;
  isRetry?: boolean;
}

type MethodlessRequestOptions = Omit<HttpRequestOptions, 'method'>;

const request = async (requestProps: HttpRequestOptions) => {
  try {
    const response = await fetchRequest(requestProps);

    if (!response.ok) {
      return await handleError(response, requestProps);
    }

    return response;
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw new AppError(404, API_ERRORS.NETWORK_UNAVAILABLE);
  }
};

const handleError = async (
  response: Response,
  requestProps: HttpRequestOptions
): Promise<Response> => {
  const responseStr = await response.clone().text();
  const errorCode = JSON.parse(responseStr).codeLightCode;

  // TOKEN_EXPIRED 처리: 재시도가 아닌 경우에만
  if (errorCode === 'TOKEN_EXPIRED' && !requestProps.isRetry) {
    const refreshed = await refreshAccessToken();
    if (refreshed) {
      // 토큰 갱신 성공 시 원래 요청 재시도
      return request({ ...requestProps, isRetry: true });
    }
  }

  const appError = new AppError(response.status, errorCode);
  throw appError;
};

const refreshAccessToken = async (): Promise<boolean> => {
  try {
    const response = await fetch(BASE_URL + ENDPOINT.TOKEN_REFRESH, {
      method: 'POST',
      credentials: 'include', // RefreshToken 쿠키 전송
    });

    if (response.ok) {
      const data = await response.json();
      useAuthStore.getState().setAccessToken(data.accessToken);
      return true;
    }
    return false;
  } catch {
    // 토큰 갱신 실패 시 로그아웃 처리
    useAuthStore.getState().clearAccessToken();
    return false;
  }
};

const fetchRequest = ({
  url,
  method,
  body,
  headers,
  params,
}: HttpRequestOptions) => {
  const finalHeaders = new Headers(headers);

  // accessToken이 있으면 Authorization 헤더에 자동 추가
  const accessToken = useAuthStore.getState().getAccessToken();

  if (accessToken) {
    finalHeaders.set('Authorization', `Bearer ${accessToken}`);
  }

  return fetch(buildUrl(url, params), {
    method,
    body: body ? JSON.stringify(body) : undefined,
    headers: finalHeaders,
  });
};

const buildUrl = (
  url: string,
  params?: Record<string, string | string[] | number>
) => {
  const u = new URL(url);
  if (params) {
    Object.entries(params).forEach(([k, v]) => {
      if (Array.isArray(v)) {
        v.forEach((vv) => u.searchParams.append(k, String(vv)));
      } else {
        u.searchParams.append(k, String(v));
      }
    });
  }
  return u;
};

const call =
  (method: HttpRequestOptions['method']) =>
  (p: MethodlessRequestOptions & { body?: unknown }) =>
    request({
      ...p,
      method,
      headers:
        (method === 'POST' || method === 'PUT' || method === 'PATCH') &&
        p.body !== null
          ? withJson(p.headers)
          : p.headers,
    });

const withJson = (h?: HeadersInit): Headers =>
  mergeHeaders(h, { 'Content-Type': 'application/json' });

const mergeHeaders = (base?: HeadersInit, extra?: HeadersInit): Headers => {
  const out = new Headers(base);
  const add = new Headers(extra);
  add.forEach((v, k) => out.set(k, v)); // extra 우선
  return out;
};

export const fetcher = {
  get: call('GET'),
  post: call('POST'),
  put: call('PUT'),
  patch: call('PATCH'),
  delete: call('DELETE'),
};

export default fetcher;
