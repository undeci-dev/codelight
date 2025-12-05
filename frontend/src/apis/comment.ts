import { BASE_URL, ENDPOINT } from '@/apis/endpoint';
import fetcher from '@/apis/fetcher';
import {
  CommentResponse,
  CommentsResponse,
  CommentCreateRequest,
  CommentUpdateRequest,
  CommentLikeResponse,
} from '@/types/comment';

export const getComments = async (
  postId: number
): Promise<CommentsResponse> => {
  const response = await fetcher.get({
    url: `${BASE_URL}${ENDPOINT.POST_COMMENTS}/${postId}/comments`,
  });
  return response.json();
};

export const getComment = async (
  commentId: number
): Promise<CommentResponse> => {
  const response = await fetcher.get({
    url: `${BASE_URL}${ENDPOINT.COMMENT}/${commentId}`,
  });
  return response.json();
};

export const createComment = async (
  postId: number,
  request: CommentCreateRequest
): Promise<CommentResponse> => {
  const response = await fetcher.post({
    url: `${BASE_URL}${ENDPOINT.COMMENT_CREATE}/${postId}/comment`,
    body: request,
  });
  return response.json();
};

export const updateComment = async (
  commentId: number,
  request: CommentUpdateRequest
): Promise<void> => {
  await fetcher.put({
    url: `${BASE_URL}${ENDPOINT.COMMENT}/${commentId}`,
    body: request,
  });
};

export const deleteComment = async (commentId: number): Promise<void> => {
  await fetcher.delete({
    url: `${BASE_URL}${ENDPOINT.COMMENT}/${commentId}`,
  });
};

export const toggleCommentLike = async (
  commentId: number
): Promise<CommentLikeResponse> => {
  const response = await fetcher.post({
    url: `${BASE_URL}${ENDPOINT.COMMENT_LIKE}/${commentId}/like`,
  });
  return response.json();
};

export const getReplies = async (
  parentId: number
): Promise<CommentsResponse> => {
  const response = await fetcher.get({
    url: `${BASE_URL}${ENDPOINT.COMMENT_REPLIES}/${parentId}/replies`,
  });
  return response.json();
};
