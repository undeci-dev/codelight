export interface ShareData {
  title: string;
  text?: string;
  url: string;
}

export type SharePlatform = 'kakao' | 'linkedin' | 'copy' | 'native';

export const getPostShareUrl = (postId: number): string => {
  const baseUrl = window.location.origin;
  return `${baseUrl}/post/${postId}`;
};

export const getLinkedInShareUrl = (data: ShareData): string => {
  const encodedUrl = encodeURIComponent(data.url);
  return `https://www.linkedin.com/sharing/share-offsite/?url=${encodedUrl}`;
};

export const copyToClipboard = async (text: string): Promise<boolean> => {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch {
    // Fallback for older browsers
    const textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.position = 'fixed';
    textarea.style.opacity = '0';
    document.body.appendChild(textarea);
    textarea.select();
    try {
      document.execCommand('copy');
      return true;
    } catch {
      return false;
    } finally {
      document.body.removeChild(textarea);
    }
  }
};

export const canUseNativeShare = (): boolean => {
  return typeof navigator !== 'undefined' && !!navigator.share;
};

export const nativeShare = async (data: ShareData): Promise<boolean> => {
  if (!canUseNativeShare()) return false;

  try {
    await navigator.share({
      title: data.title,
      text: data.text,
      url: data.url,
    });
    return true;
  } catch (error) {
    // 사용자가 공유를 취소한 경우
    if (error instanceof Error && error.name === 'AbortError') {
      return false;
    }
    throw error;
  }
};

export const openShareWindow = (url: string): void => {
  window.open(url, '_blank', 'width=600,height=400,noopener,noreferrer');
};

export const shareToLinkedIn = (data: ShareData): void => {
  openShareWindow(getLinkedInShareUrl(data));
};

declare global {
  interface Window {
    Kakao?: {
      init: (appKey: string) => void;
      isInitialized: () => boolean;
      Share: {
        sendDefault: (options: {
          objectType: string;
          content: {
            title: string;
            description?: string;
            imageUrl?: string;
            link: {
              mobileWebUrl: string;
              webUrl: string;
            };
          };
          buttons?: Array<{
            title: string;
            link: {
              mobileWebUrl: string;
              webUrl: string;
            };
          }>;
        }) => void;
      };
    };
  }
}

export const initKakaoSDK = (appKey: string): void => {
  if (window.Kakao && !window.Kakao.isInitialized()) {
    window.Kakao.init(appKey);
  }
};

export const isKakaoInitialized = (): boolean => {
  return !!window.Kakao?.isInitialized();
};

export const shareToKakao = (
  data: ShareData & { imageUrl?: string }
): boolean => {
  if (!window.Kakao || !window.Kakao.isInitialized()) {
    console.warn('Kakao SDK is not initialized');
    return false;
  }

  window.Kakao.Share.sendDefault({
    objectType: 'feed',
    content: {
      title: data.title,
      description: data.text,
      imageUrl: data.imageUrl || `${window.location.origin}/og-image.png`,
      link: {
        mobileWebUrl: data.url,
        webUrl: data.url,
      },
    },
    buttons: [
      {
        title: '자세히 보기',
        link: {
          mobileWebUrl: data.url,
          webUrl: data.url,
        },
      },
    ],
  });

  return true;
};
