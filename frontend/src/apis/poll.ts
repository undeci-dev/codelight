import fetcher from '@/apis/fetcher';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';
import { PollResponse, PollVoteRequest, PollUpdateRequest } from '@/types/poll';

export const getPollByPost = async (postId: number): Promise<PollResponse> => {
  const response = await fetcher.get({
    url: `${BASE_URL}${ENDPOINT.POLL_BY_POST}/${postId}/poll`,
  });
  return response.json();
};

export const getPoll = async (pollId: number): Promise<PollResponse> => {
  const response = await fetcher.get({
    url: `${BASE_URL}${ENDPOINT.POLL}/${pollId}`,
  });
  return response.json();
};

export const votePoll = async (
  pollId: number,
  request: PollVoteRequest
): Promise<PollResponse> => {
  const response = await fetcher.post({
    url: `${BASE_URL}${ENDPOINT.POLL_VOTE}/${pollId}/vote`,
    body: request,
  });
  return response.json();
};

export const cancelVote = async (pollId: number): Promise<void> => {
  await fetcher.delete({
    url: `${BASE_URL}${ENDPOINT.POLL}/${pollId}/vote`,
  });
};

export const updatePoll = async (
  pollId: number,
  request: PollUpdateRequest
): Promise<PollResponse> => {
  const response = await fetcher.put({
    url: `${BASE_URL}${ENDPOINT.POLL}/${pollId}`,
    body: request,
  });
  return response.json();
};

export const deletePoll = async (pollId: number): Promise<void> => {
  await fetcher.delete({
    url: `${BASE_URL}${ENDPOINT.POLL}/${pollId}`,
  });
};
