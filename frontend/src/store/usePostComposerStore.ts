import { create } from 'zustand';
import type { LinkPreviewResponse } from '@/types/link';
import type { FileUploadState } from '@/types/file';
import type { PollCreateRequest } from '@/types/poll';
import { fetchLinkPreview } from '@/apis/link';

interface PostComposerState {
  content: string;
  detectedLinks: LinkPreviewResponse[];
  fetchedUrls: Set<string>;
  isLoading: boolean;
  showEmojiPicker: boolean;
  showFileUpload: boolean;
  showPollCreator: boolean;
  uploadedFiles: FileUploadState[];
  poll: PollCreateRequest | null;

  setContent: (content: string) => void;
  handleContentChange: (content: string) => void;
  setDetectedLinks: (links: LinkPreviewResponse[]) => void;
  removeLinkPreview: (url: string) => void;
  setIsLoading: (loading: boolean) => void;
  toggleEmojiPicker: () => void;
  toggleFileUpload: () => void;
  togglePollCreator: () => void;
  setUploadedFiles: (files: FileUploadState[]) => void;
  setPoll: (poll: PollCreateRequest | null) => void;
  handleEmojiSelect: (emoji: string) => void;
  resetForm: () => void;
  canSubmit: (maxLength: number) => boolean;
}

const usePostComposerStore = create<PostComposerState>((set, get) => ({
  content: '',
  detectedLinks: [],
  fetchedUrls: new Set<string>(),
  isLoading: false,
  showEmojiPicker: false,
  showFileUpload: false,
  showPollCreator: false,
  uploadedFiles: [],
  poll: null,

  setContent: (content) => set({ content }),

  handleContentChange: async (content) => {
    set({ content });

    // URL 감지 로직
    const urlRegex = /(https?:\/\/[^\s]+)/g;
    const urls = content.match(urlRegex) || [];

    if (urls.length === 0) {
      set({ detectedLinks: [] });
      return;
    }

    const state = get();
    const newUrls = urls.filter((url) => !state.fetchedUrls.has(url));

    if (newUrls.length === 0) {
      // 이미 감지된 URL 중 현재 content에 남아있는 것만 유지
      const currentUrls = new Set(urls);
      set({
        detectedLinks: state.detectedLinks.filter((link) =>
          currentUrls.has(link.url)
        ),
      });
      return;
    }

    // 새 URL에 대해 API 호출
    set({ isLoading: true });

    try {
      const newLinkPreviews = await Promise.all(
        newUrls.map(async (url) => {
          try {
            return await fetchLinkPreview({ url });
          } catch {
            // API 실패 시 기본 정보만 반환
            return {
              url,
              domain: new URL(url).hostname,
            };
          }
        })
      );

      const updatedState = get();
      const currentUrls = new Set(urls);

      // 기존 링크 중 현재 content에 남아있는 것 + 새로 가져온 링크
      const existingLinks = updatedState.detectedLinks.filter((link) =>
        currentUrls.has(link.url)
      );

      const allLinks = [
        ...existingLinks,
        ...newLinkPreviews.filter(
          (newLink) => !existingLinks.some((link) => link.url === newLink.url)
        ),
      ];

      set({
        detectedLinks: allLinks,
        fetchedUrls: new Set([...updatedState.fetchedUrls, ...newUrls]),
        isLoading: false,
      });
    } catch {
      set({ isLoading: false });
    }
  },

  setDetectedLinks: (links) => set({ detectedLinks: links }),

  removeLinkPreview: (url) => {
    const state = get();
    set({
      detectedLinks: state.detectedLinks.filter((link) => link.url !== url),
    });
  },

  setIsLoading: (loading) => set({ isLoading: loading }),

  toggleEmojiPicker: () => {
    const state = get();
    set({ showEmojiPicker: !state.showEmojiPicker });
  },

  toggleFileUpload: () => {
    const state = get();
    set({ showFileUpload: !state.showFileUpload });
  },

  togglePollCreator: () => {
    const state = get();
    set({ showPollCreator: !state.showPollCreator });
  },

  setUploadedFiles: (files) => set({ uploadedFiles: files }),

  setPoll: (poll) => set({ poll }),

  handleEmojiSelect: (emoji) => {
    const state = get();
    set({ content: state.content + emoji, showEmojiPicker: false });
  },

  resetForm: () => {
    set({
      content: '',
      detectedLinks: [],
      fetchedUrls: new Set<string>(),
      isLoading: false,
      showEmojiPicker: false,
      showFileUpload: false,
      showPollCreator: false,
      uploadedFiles: [],
      poll: null,
    });
  },

  canSubmit: (maxLength) => {
    const state = get();
    return (
      state.content.trim().length > 0 &&
      state.content.length <= maxLength &&
      !state.isLoading
    );
  },
}));

export default usePostComposerStore;
