export interface LinkPreviewRequest {
  url: string;
}

export interface LinkPreviewResponse {
  linkId?: number;
  url: string;
  title?: string;
  description?: string;
  image?: string;
  domain?: string;
}

export interface LinkCreateRequest {
  url: string;
}

export interface LinkUpdateRequest {
  url: string;
}
