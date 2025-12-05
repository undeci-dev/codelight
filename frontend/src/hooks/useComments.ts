import { useState } from 'react';
import { formatRelativeTime } from '@/utils/feed/commentUtils';

export const useComments = () => {
  const [newComment, setNewComment] = useState('');
  const [replyingTo, setReplyingTo] = useState<number | null>(null);
  const [replyContent, setReplyContent] = useState('');

  const handleReplyClick = (commentId: number) => {
    if (replyingTo === commentId) {
      setReplyingTo(null);
      setReplyContent('');
    } else {
      setReplyingTo(commentId);
      setReplyContent('');
    }
  };

  const handleCancelReply = () => {
    setReplyingTo(null);
    setReplyContent('');
  };

  return {
    newComment,
    setNewComment,
    replyingTo,
    replyContent,
    setReplyContent,
    formatTime: formatRelativeTime,
    handleReplyClick,
    handleCancelReply,
  };
};
