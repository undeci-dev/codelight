import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useAppMutation } from '@/hooks/useAppMutation';
import {
  getComments,
  createComment,
  updateComment,
  deleteComment,
  toggleCommentLike,
} from '@/apis/comment';
import { CommentCreateRequest, CommentUpdateRequest } from '@/types/comment';

export const useCommentsQuery = (postId: number) => {
  return useQuery({
    queryKey: ['comments', postId],
    queryFn: () => getComments(postId),
    enabled: !!postId,
  });
};

export const useCreateCommentMutation = (postId: number) => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['comments', 'create', postId],
    mutationFn: (request: CommentCreateRequest) =>
      createComment(postId, request),
    successMessage: '댓글이 작성되었습니다.',
    errorMessage: '댓글 작성에 실패했습니다.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['comments', postId] });
    },
  });
};

export const useUpdateCommentMutation = (postId: number) => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['comments', 'update', postId],
    mutationFn: ({
      commentId,
      request,
    }: {
      commentId: number;
      request: CommentUpdateRequest;
    }) => updateComment(commentId, request),
    successMessage: '댓글이 수정되었습니다.',
    errorMessage: '댓글 수정에 실패했습니다.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['comments', postId] });
    },
  });
};

export const useDeleteCommentMutation = (postId: number) => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['comments', 'delete', postId],
    mutationFn: (commentId: number) => deleteComment(commentId),
    successMessage: '댓글이 삭제되었습니다.',
    errorMessage: '댓글 삭제에 실패했습니다.',
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['comments', postId] });
    },
  });
};

export const useToggleCommentLikeMutation = (postId: number) => {
  const queryClient = useQueryClient();

  return useAppMutation({
    mutationKey: ['comments', 'like', postId],
    mutationFn: (commentId: number) => toggleCommentLike(commentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['comments', postId] });
    },
  });
};
