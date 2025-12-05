import fetcher from '@/apis/fetcher';
import { BASE_URL, ENDPOINT } from '@/apis/endpoint';
import { LinkPreviewRequest, LinkPreviewResponse } from '@/types/link';

export const fetchLinkPreview = async (
  request: LinkPreviewRequest
): Promise<LinkPreviewResponse> => {
  const response = await fetcher.post({
    url: BASE_URL + ENDPOINT.LINK_PREVIEW,
    body: request,
  });
  return response.json();
};
