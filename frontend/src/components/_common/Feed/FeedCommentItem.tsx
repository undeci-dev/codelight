import { useState } from 'react';
import {
  Heart,
  MessageCircle,
  Edit2,
  Trash2,
  MoreVertical,
} from 'lucide-react';
import Avatar from '@/components/_common/Avatar/Avatar';
import Text from '@/components/_common/Text/Text';
import FeedCommentInput from './FeedCommentInput';
import type { CommentResponse } from '@/types/comment';
import { formatRelativeTime } from '@/utils/feed/commentUtils';

interface FeedCommentItemProps {
  comment: CommentResponse;
  replyingTo: number | null;
  replyContent: string;
  onReplyClick: (commentId: number) => void;
  onLikeComment: (commentId: number) => void;
  onReplyContentChange: (content: string) => void;
  onAddReply: (parentId: number) => void;
  onCancelReply: () => void;
  onUpdateComment?: (commentId: number, content: string) => void;
  onDeleteComment?: (commentId: number) => void;
  depth?: number;
}

const FeedCommentItem = ({
  comment,
  replyingTo,
  replyContent,
  onReplyClick,
  onLikeComment,
  onReplyContentChange,
  onAddReply,
  onCancelReply,
  onUpdateComment,
  onDeleteComment,
  depth = 0,
}: FeedCommentItemProps) => {
  const [showMenu, setShowMenu] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(comment.content);

  const isReplying = replyingTo === comment.commentId;
  const maxDepth = 3;
  const canReply = depth < maxDepth;

  const handleEditStart = () => {
    setEditContent(comment.content);
    setIsEditing(true);
    setShowMenu(false);
  };

  const handleEditCancel = () => {
    setIsEditing(false);
    setEditContent(comment.content);
  };

  const handleEditSubmit = () => {
    if (!editContent.trim() || !onUpdateComment) return;
    onUpdateComment(comment.commentId, editContent.trim());
    setIsEditing(false);
  };

  const handleDelete = () => {
    if (!onDeleteComment) return;
    onDeleteComment(comment.commentId);
    setShowMenu(false);
  };

  return (
    <div className={`${depth > 0 ? 'ml-12' : ''}`}>
      <div className='flex gap-3 py-3'>
        <Avatar
          name={comment.userName}
          src={comment.profileImageUrl}
          size='sm'
        />

        <div className='flex-1'>
          <div className='flex items-start justify-between'>
            <div className='flex-1'>
              <Text
                typography='body2'
                color='gray900'
                className='font-semibold'
              >
                {comment.userName}
              </Text>
              {isEditing ? (
                <div className='mt-2 space-y-2'>
                  <textarea
                    value={editContent}
                    onChange={(e) => setEditContent(e.target.value)}
                    className='w-full resize-none rounded-lg border border-gray-300 p-2 text-sm focus:border-purple-500 focus:outline-none'
                    rows={2}
                  />
                  <div className='flex gap-2'>
                    <button
                      onClick={handleEditSubmit}
                      disabled={!editContent.trim()}
                      className='rounded-lg bg-purple-600 px-3 py-1 text-xs text-white hover:bg-purple-700 disabled:opacity-50'
                    >
                      수정
                    </button>
                    <button
                      onClick={handleEditCancel}
                      className='rounded-lg px-3 py-1 text-xs text-gray-600 hover:bg-gray-100'
                    >
                      취소
                    </button>
                  </div>
                </div>
              ) : (
                <Text typography='body2' color='gray700' className='mt-1'>
                  {comment.content}
                </Text>
              )}
            </div>

            {comment.isOwner && !isEditing && (
              <div className='relative'>
                <button
                  onClick={() => setShowMenu(!showMenu)}
                  className='rounded-full p-1 transition-colors hover:bg-gray-100'
                >
                  <MoreVertical size={16} className='text-gray-400' />
                </button>

                {showMenu && (
                  <div className='absolute right-0 top-6 z-10 w-24 rounded-lg border border-gray-200 bg-white py-1 shadow-lg'>
                    <button
                      onClick={handleEditStart}
                      className='flex w-full items-center gap-2 px-3 py-1.5 text-left text-xs text-gray-700 hover:bg-gray-100'
                    >
                      <Edit2 size={12} />
                      수정
                    </button>
                    <button
                      onClick={handleDelete}
                      className='flex w-full items-center gap-2 px-3 py-1.5 text-left text-xs text-red-600 hover:bg-gray-100'
                    >
                      <Trash2 size={12} />
                      삭제
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

          <div className='mt-2 flex items-center gap-4'>
            <Text typography='caption' color='gray500'>
              {formatRelativeTime(comment.createdAt)}
            </Text>

            <button
              onClick={() => onLikeComment(comment.commentId)}
              className={`flex items-center gap-1 ${
                comment.liked
                  ? 'text-red-500'
                  : 'text-gray-500 hover:text-red-500'
              }`}
            >
              <Heart size={14} fill={comment.liked ? 'currentColor' : 'none'} />
              {comment.likesCount > 0 && (
                <Text
                  typography='caption'
                  color={comment.liked ? 'error' : 'gray600'}
                >
                  {comment.likesCount}
                </Text>
              )}
            </button>

            {canReply && (
              <button
                onClick={() => onReplyClick(comment.commentId)}
                className='flex items-center gap-1 text-gray-500 hover:text-purple-600'
              >
                <MessageCircle size={14} />
                <Text typography='caption' color='gray600'>
                  답글
                </Text>
              </button>
            )}
          </div>

          {isReplying && (
            <div className='mt-3'>
              <FeedCommentInput
                value={replyContent}
                onChange={onReplyContentChange}
                onSubmit={() => onAddReply(comment.commentId)}
                placeholder={`${comment.userName}님에게 답글...`}
                autoFocus
                size='sm'
              />
              <button
                onClick={onCancelReply}
                className='mt-2 text-sm text-gray-500 hover:text-gray-700'
              >
                취소
              </button>
            </div>
          )}
        </div>
      </div>

      {comment.replies && comment.replies.length > 0 && (
        <div>
          {comment.replies.map((reply) => (
            <FeedCommentItem
              key={reply.commentId}
              comment={reply}
              replyingTo={replyingTo}
              replyContent={replyContent}
              onReplyClick={onReplyClick}
              onLikeComment={onLikeComment}
              onReplyContentChange={onReplyContentChange}
              onAddReply={onAddReply}
              onCancelReply={onCancelReply}
              onUpdateComment={onUpdateComment}
              onDeleteComment={onDeleteComment}
              depth={depth + 1}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default FeedCommentItem;
