import {
  PollCreateRequest,
  PollUpdateRequest,
  PollResponse,
} from '@/types/poll';
import {
  LinkCreateRequest,
  LinkUpdateRequest,
  LinkPreviewResponse,
} from '@/types/link';
import { FileResponse } from '@/types/file';

export interface PostsResponse {
  posts: PostSummaryResponse[];
  hasNext: boolean;
  nextCursor?: string;
}

export interface PostSummaryResponse {
  postId: number;
  userName: string;
  profileImageUrl?: string;
  contentPreview: string;
  likesCount: number;
  commentsCount: number;
  sharesCount?: number;
  createdAt: string;
  files?: FileResponse[];
  liked?: boolean;
  poll?: PollResponse;
  links?: LinkPreviewResponse[];
}

export interface PostDetailResponse {
  postId: number;
  userId: number;
  userName: string;
  profileImageUrl?: string;
  content: string;
  likesCount: number;
  commentsCount: number;
  sharesCount?: number;
  createdAt: string;
  files?: FileResponse[];
  isOwner?: boolean;
  liked?: boolean;
  poll?: PollResponse;
  links?: LinkPreviewResponse[];
}

export interface PostCreateRequest {
  content: string;
  poll?: PollCreateRequest;
  links?: LinkCreateRequest[];
}

// 기존 파일 순서 업데이트 정보
export interface FileOrderUpdate {
  fileId: number;
  displayOrder: number;
}

export interface PostUpdateRequest {
  content: string;
  poll?: PollUpdateRequest;
  links?: LinkUpdateRequest[];
  deleteFileIds?: number[];
  fileOrders?: FileOrderUpdate[];
  deleteLinkIds?: number[];
}
