import {
  useQuery,
  type UseQueryOptions,
  type QueryKey,
} from '@tanstack/react-query';
import AppError from '@/core/error/AppError';

interface ApiError extends Error {
  statusCode?: number;
  errorMessage: string;
  errorCode?: string;
}

type AppQueryOptions<TData, TError> = Omit<
  UseQueryOptions<TData, TError, TData, QueryKey>,
  'queryKey' | 'queryFn'
> & {
  queryKey: QueryKey;
  queryFn: () => Promise<TData>;
};

export function isAppError(error: unknown): error is AppError {
  return error instanceof AppError;
}

export function useAppQuery<
  TData = unknown,
  TError extends ApiError = ApiError,
>(opts: AppQueryOptions<TData, TError>) {
  const { queryKey, queryFn, ...rest } = opts;

  return useQuery<TData, TError, TData, QueryKey>({
    queryKey,
    queryFn,
    ...rest,
  });
}
