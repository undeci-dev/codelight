import AppError from '@/core/error/AppError';
import { API_ERRORS } from '@/constants/messages/apiErrors';

interface HttpRequestOptions {
  url: string;
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: object;
  headers?: HeadersInit;
  params?: Record<string, string | string[] | number>;
}

type MethodlessRequestOptions = Omit<HttpRequestOptions, 'method'>;

const request = async (requestProps: HttpRequestOptions) => {
  try {
    const response = await fetchRequest(requestProps);

    if (!response.ok) {
      await handleError(response);
    }

    return response;
  } catch (error) {
    if (error instanceof AppError) throw error;
    throw new AppError(404, API_ERRORS.NETWORK_UNAVAILABLE);
  }
};

const handleError = async (response: Response) => {
  const responseStr = await response.clone().text();
  const errorCode = JSON.parse(responseStr).codeLightCode;
  const appError = new AppError(response.status, errorCode);

  throw appError;
};

const fetchRequest = ({
  url,
  method,
  body,
  headers,
  params,
}: HttpRequestOptions) => {
  const finalHeaders = new Headers(headers);
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
