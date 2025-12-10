import fetcher from '@/apis/fetcher';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';
import {
  PostsResponse,
  PostDetailResponse,
  PostCreateRequest,
  PostUpdateRequest,
} from '@/types/post';

export interface GetPostsParams {
  lastPostId?: number;
  keyword?: string;
}

export const getPosts = async (
  params?: GetPostsParams
): Promise<PostsResponse> => {
  const searchParams = new URLSearchParams();

  if (params?.lastPostId) {
    searchParams.append('lastPostId', params.lastPostId.toString());
  }
  if (params?.keyword) {
    searchParams.append('keyword', params.keyword);
  }

  const queryString = searchParams.toString();
  const url = `${BASE_URL}${ENDPOINT.POSTS}${queryString ? `?${queryString}` : ''}`;

  const response = await fetcher.get({ url });
  return response.json();
};

export const getPost = async (postId: number): Promise<PostDetailResponse> => {
  const response = await fetcher.get({
    url: `${BASE_URL}${ENDPOINT.POST}/${postId}`,
  });
  return response.json();
};

export const createPost = async (
  postData: PostCreateRequest,
  files?: File[]
) => {
  const formData = new FormData();

  formData.append(
    'request',
    new Blob([JSON.stringify(postData)], { type: 'application/json' })
  );

  if (files && files.length > 0) {
    files.forEach((file) => {
      formData.append('files', file);
    });
  }

  return await fetcher.post({
    url: BASE_URL + ENDPOINT.POST_CREATE,
    body: formData,
  });
};

export const updatePost = async (
  postId: number,
  postData: PostUpdateRequest,
  files?: File[]
): Promise<void> => {
  const formData = new FormData();

  formData.append(
    'request',
    new Blob([JSON.stringify(postData)], { type: 'application/json' })
  );

  if (files && files.length > 0) {
    files.forEach((file) => {
      formData.append('files', file);
    });
  }
  await fetcher.put({
    url: `${BASE_URL}${ENDPOINT.POST}/${postId}`,
    body: formData,
  });
};

export const deletePost = async (postId: number): Promise<void> => {
  await fetcher.delete({
    url: `${BASE_URL}${ENDPOINT.POST}/${postId}`,
  });
};

export const likePost = async (postId: number): Promise<void> => {
  await fetcher.post({
    url: `${BASE_URL}${ENDPOINT.POST}/${postId}/like`,
  });
};
