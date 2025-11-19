import {
  useMutation,
  type UseMutationOptions,
  type MutationKey,
} from '@tanstack/react-query';
import useToast from '@/hooks/useToast';
import AppError from '@/core/error/AppError';

interface ApiError extends Error {
  statusCode?: number;
  errorMessage: string;
  errorCode?: string;
}

/**
 * useAppMutation 옵션 타입
 * - mutationKey와 mutationFn은 필수
 * - 토스트 메시지와 에러 처리 옵션 추가
 */
type AppMutationOptions<TData, TError, TVariables, TContext> = Omit<
  UseMutationOptions<TData, TError, TVariables, TContext>,
  'mutationKey' | 'mutationFn'
> & {
  mutationKey: MutationKey;
  mutationFn: (variables: TVariables) => Promise<TData>;
  successMessage?: string;
  errorMessage?: string;
  throwOnError?: boolean;
};

export function isAppError(error: unknown): error is AppError {
  return error instanceof AppError;
}

/**
 * TanStack Query의 useMutation을 래핑한 커스텀 훅
 * - 성공/실패 시 자동 토스트 메시지 표시
 * - 에러 바운더리 처리 옵션
 * - onSuccess/onError 콜백 체이닝 지원
 */
export function useAppMutation<
  TData = unknown,
  TError extends ApiError = ApiError,
  TVariables = void,
  TContext = unknown,
>(opts: AppMutationOptions<TData, TError, TVariables, TContext>) {
  const { addToast } = useToast();
  const {
    mutationKey,
    mutationFn,
    successMessage,
    errorMessage,
    throwOnError = false,
    onSuccess,
    onError,
    ...rest
  } = opts;

  return useMutation<TData, TError, TVariables, TContext>({
    mutationKey,
    mutationFn,
    throwOnError,
    ...rest,
    onSuccess: (...args) => {
      if (successMessage) {
        addToast({ message: successMessage, type: 'success' });
      }
      onSuccess?.(...args);
    },
    onError: (error, ...args) => {
      const customMessage = isAppError(error)
        ? error.errorMessage
        : errorMessage;

      if (customMessage) {
        addToast({ message: customMessage, type: 'error' });
      }
      onError?.(error, ...args);
    },
  });
}
