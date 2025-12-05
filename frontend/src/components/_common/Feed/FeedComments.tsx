import Modal from '@/components/_common/Modal/Modal';
import Text from '@/components/_common/Text/Text';
import FeedCommentItem from './FeedCommentItem';
import FeedCommentInput from './FeedCommentInput';
import { useComments } from '@/hooks/useComments';
import {
  useCommentsQuery,
  useCreateCommentMutation,
  useUpdateCommentMutation,
  useDeleteCommentMutation,
  useToggleCommentLikeMutation,
} from '@/hooks/query/useCommentsQuery';

interface FeedCommentsProps {
  postId: number;
  onClose: () => void;
}

const FeedComments = ({ postId, onClose }: FeedCommentsProps) => {
  const {
    newComment,
    setNewComment,
    replyingTo,
    replyContent,
    setReplyContent,
    handleReplyClick,
    handleCancelReply,
  } = useComments();

  const { data: commentsData } = useCommentsQuery(postId);
  const createCommentMutation = useCreateCommentMutation(postId);
  const updateCommentMutation = useUpdateCommentMutation(postId);
  const deleteCommentMutation = useDeleteCommentMutation(postId);
  const toggleLikeMutation = useToggleCommentLikeMutation(postId);

  const comments = commentsData?.comments ?? [];
  const totalComments = commentsData?.totalCount ?? 0;

  const handleCommentSubmit = () => {
    if (!newComment.trim()) return;
    createCommentMutation.mutate(
      { content: newComment.trim() },
      {
        onSuccess: () => setNewComment(''),
      }
    );
  };

  const handleReplySubmit = (parentId: number) => {
    if (!replyContent.trim()) return;
    createCommentMutation.mutate(
      { content: replyContent.trim(), parentId },
      {
        onSuccess: () => {
          setReplyContent('');
          handleCancelReply();
        },
      }
    );
  };

  const handleLikeComment = (commentId: number) => {
    toggleLikeMutation.mutate(commentId);
  };

  const handleUpdateComment = (commentId: number, content: string) => {
    updateCommentMutation.mutate({ commentId, request: { content } });
  };

  const handleDeleteComment = (commentId: number) => {
    if (!confirm('댓글을 삭제하시겠습니까?')) return;
    deleteCommentMutation.mutate(commentId);
  };

  return (
    <Modal
      isOpen={true}
      onClose={onClose}
      title={`댓글 ${totalComments}개`}
      size='xl'
    >
      <div className='p-4'>
        {comments.length === 0 ? (
          <div className='flex flex-col items-center justify-center py-12'>
            <Text typography='body1' color='gray500'>
              첫 댓글을 작성해보세요!
            </Text>
          </div>
        ) : (
          <div className='space-y-1'>
            {comments.map((comment) => (
              <FeedCommentItem
                key={comment.commentId}
                comment={comment}
                replyingTo={replyingTo}
                replyContent={replyContent}
                onReplyClick={handleReplyClick}
                onLikeComment={handleLikeComment}
                onReplyContentChange={setReplyContent}
                onAddReply={handleReplySubmit}
                onCancelReply={handleCancelReply}
                onUpdateComment={handleUpdateComment}
                onDeleteComment={handleDeleteComment}
              />
            ))}
          </div>
        )}
      </div>

      <div className='border-t border-gray-200 p-4'>
        <FeedCommentInput
          value={newComment}
          onChange={setNewComment}
          onSubmit={handleCommentSubmit}
        />
      </div>
    </Modal>
  );
};

export default FeedComments;
