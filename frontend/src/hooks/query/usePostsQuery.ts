import { useQueryClient } from '@tanstack/react-query';
import { useAppMutation } from '@/hooks/useAppMutation';
import {
  getPosts,
  getPost,
  createPost,
  updatePost,
  deletePost,
  likePost,
} from '@/apis/post';
import { votePoll, cancelVote } from '@/apis/poll';
import { useAppQuery } from '@/hooks/useAppQuery';
import { PostCreateRequest, PostUpdateRequest } from '@/types/post';

export const useCreatePostMutation = () => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['posts', 'submit'],
    mutationFn: ({
      postData,
      files,
    }: {
      postData: PostCreateRequest;
      files?: File[];
    }) => createPost(postData, files),
    successMessage: '글이 작성되었습니다',
    errorMessage: '글 작성에 실패했습니다. 잠시 후 다시 시도해주세요.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] });
    },
  });
};

export const useUpdatePostMutation = () => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['posts', 'update'],
    mutationFn: ({
      postId,
      data,
      files,
    }: {
      postId: number;
      data: PostUpdateRequest;
      files?: File[];
    }) => updatePost(postId, data, files),
    successMessage: '글이 수정되었습니다',
    errorMessage: '글 수정에 실패했습니다. 잠시 후 다시 시도해주세요.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] });
    },
  });
};

export const useDeletePostMutation = () => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['posts', 'delete'],
    mutationFn: (postId: number) => deletePost(postId),
    successMessage: '글이 삭제되었습니다',
    errorMessage: '글 삭제에 실패했습니다. 잠시 후 다시 시도해주세요.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] });
    },
  });
};

export const useLikePostMutation = () => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['posts', 'like'],
    mutationFn: (postId: number) => likePost(postId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] });
    },
  });
};

export const useSelectPostQuery = (postId: number) => {
  return useAppQuery({
    queryKey: ['posts', postId],
    queryFn: () => getPost(postId),
  });
};

export const useSelectPostsQuery = () => {
  return useAppQuery({
    queryKey: ['posts', 'select'],
    queryFn: getPosts,
  });
};

export const useVotePollMutation = () => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['poll', 'vote'],
    mutationFn: ({
      pollId,
      optionIds,
    }: {
      pollId: number;
      optionIds: number[];
    }) => votePoll(pollId, { optionIds }),
    successMessage: '투표가 완료되었습니다',
    errorMessage: '투표에 실패했습니다. 잠시 후 다시 시도해주세요.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] });
    },
  });
};

export const useCancelVoteMutation = () => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['poll', 'cancelVote'],
    mutationFn: (pollId: number) => cancelVote(pollId),
    successMessage: '투표가 취소되었습니다',
    errorMessage: '투표 취소에 실패했습니다. 잠시 후 다시 시도해주세요.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['posts'] });
    },
  });
};
