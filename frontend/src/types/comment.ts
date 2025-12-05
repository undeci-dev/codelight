export interface CommentResponse {
  commentId: number;
  userId: number;
  userName: string;
  profileImageUrl?: string;
  content: string;
  likesCount: number;
  createdAt: string;
  updatedAt: string;
  isOwner: boolean;
  liked: boolean;
  parentId: number;
  replies: CommentResponse[];
}

export interface CommentsResponse {
  comments: CommentResponse[];
  totalCount: number;
}

export interface CommentCreateRequest {
  content: string;
  parentId?: number;
}

export interface CommentUpdateRequest {
  content: string;
}

export interface CommentLikeResponse {
  liked: boolean;
}
