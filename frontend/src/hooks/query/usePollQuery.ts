import { useQueryClient } from '@tanstack/react-query';
import { useAppQuery } from '@/hooks/useAppQuery';
import { useAppMutation } from '@/hooks/useAppMutation';
import { getPoll, getPollByPost, votePoll } from '@/apis/poll';
import { PollVoteRequest } from '@/types/poll';

export const usePollByPostQuery = (postId: number) => {
  return useAppQuery({
    queryKey: ['poll', 'post', postId],
    queryFn: () => getPollByPost(postId),
    enabled: !!postId,
  });
};

export const usePollQuery = (pollId: number) => {
  return useAppQuery({
    queryKey: ['poll', pollId],
    queryFn: () => getPoll(pollId),
    enabled: !!pollId,
  });
};

export const useVotePollMutation = (postId?: number) => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['poll', 'vote'],
    mutationFn: ({
      pollId,
      request,
    }: {
      pollId: number;
      request: PollVoteRequest;
    }) => votePoll(pollId, request),
    successMessage: '투표가 완료되었습니다',
    errorMessage: '투표에 실패했습니다. 잠시 후 다시 시도해주세요.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['poll'] });
      if (postId) {
        queryClient.invalidateQueries({ queryKey: ['poll', 'post', postId] });
      }
    },
  });
};
