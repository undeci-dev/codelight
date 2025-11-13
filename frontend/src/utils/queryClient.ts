import { QueryClient } from '@tanstack/react-query';

/**
 * TanStack Query 클라이언트 인스턴스
 * - 전역 캐싱 및 상태 관리
 */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
      staleTime: 1000 * 60 * 5,
    },
    mutations: {
      retry: 0,
    },
  },
});
