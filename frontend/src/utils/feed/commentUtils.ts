import type { CommentResponse } from '@/types/comment';

/**
 * 전체 댓글 수 계산 (대댓글 포함)
 */
export const getTotalCommentCount = (comments: CommentResponse[]): number => {
  let count = comments.length;
  comments.forEach((comment) => {
    if (comment.replies && comment.replies.length > 0) {
      count += getTotalCommentCount(comment.replies);
    }
  });
  return count;
};

/**
 * 상대 시간 계산
 */
export const formatRelativeTime = (timestamp: string): string => {
  // 타입 가드: 문자열인지 확인
  if (typeof timestamp !== 'string' || !timestamp) {
    return '날짜 오류';
  }

  let date: Date;

  // 1. 숫자로 변환 시도 (타임스탬프)
  const num = Number(timestamp);
  if (!isNaN(num)) {
    date = new Date(num);
  } else {
    // 2. 문자열 파싱 (YYYYMMDDHHMMSS...)
    const isoDate = timestamp.replace(
      /(\d{4})(\d{2})(\d{2})(\d{2})(\d{2})(\d{2})/,
      '$1-$2-$3T$4:$5:$6Z'
    );
    date = new Date(isoDate);
  }

  if (isNaN(date.getTime())) {
    return '날짜 오류';
  }

  const now = new Date();
  const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);

  if (diffInSeconds < 60) return '방금 전';
  const diffInMinutes = Math.floor(diffInSeconds / 60);
  if (diffInMinutes < 60) return `${diffInMinutes}분 전`;
  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24) return `${diffInHours}시간 전`;
  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays < 7) return `${diffInDays}일 전`;
  const diffInWeeks = Math.floor(diffInDays / 7);
  if (diffInWeeks < 4) return `${diffInWeeks}주 전`;
  const diffInMonths = Math.floor(diffInDays / 30);
  if (diffInMonths < 12) return `${diffInMonths}개월 전`;
  const diffInYears = Math.floor(diffInDays / 365);
  return `${diffInYears}년 전`;
};

export const formatAbsoluteTime = (isoString: string): string => {
  const date = new Date(isoString);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  const hours = date.getHours().toString().padStart(2, '0');
  const minutes = date.getMinutes().toString().padStart(2, '0');

  return `${year}-${month}-${day} ${hours}:${minutes}`;
};
