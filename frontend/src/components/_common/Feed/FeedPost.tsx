import { HTMLAttributes } from 'react';
import { useNavigate } from 'react-router-dom';
import { MessageSquare, Heart, Share2 } from 'lucide-react';
import Text from '@/components/_common/Text/Text';
import FeedPoll from './FeedPoll';
import FeedLinkPreviewList from './FeedLinkPreviewList';
import { PostSummaryResponse } from '@/types/post';

interface FeedPostProps
  extends PostSummaryResponse,
    HTMLAttributes<HTMLDivElement> {
  onLike?: () => void;
  onShare?: () => void;
  onVote?: (optionIds: number[]) => void;
  onCancelVote?: (pollId: number) => void;
}

const FeedPost = ({
  postId,
  userName,
  profileImageUrl,
  contentPreview,
  createdAt,
  likesCount = 0,
  commentsCount = 0,
  sharesCount = 0,
  onLike,
  onShare,
  onVote,
  onCancelVote,
  liked = false,
  className = '',
  files,
  poll,
  links,
  ...rest
}: FeedPostProps) => {
  const navigate = useNavigate();
  const handleClick = (e: React.MouseEvent) => {
    // 버튼 클릭 시에는 상세 페이지로 이동하지 않음
    if ((e.target as HTMLElement).closest('button')) {
      return;
    }
    navigate(`/post/${postId}`);
  };

  return (
    <div
      className={`cursor-pointer bg-white p-4 transition-colors hover:bg-gray-50 ${className}`}
      onClick={handleClick}
      {...rest}
    >
      <div className='mb-3 flex items-center gap-3'>
        <div className='flex h-10 w-10 items-center justify-center rounded-full bg-gray-300'>
          {profileImageUrl ? (
            <img
              src={profileImageUrl}
              alt={userName}
              className='h-full w-full rounded-full object-cover'
            />
          ) : (
            <Text typography='body2' color='white'>
              {userName.charAt(0)}
            </Text>
          )}
        </div>
        <div className='flex-1'>
          <Text typography='body1' color='gray900' className='font-semibold'>
            {userName}
          </Text>
          <Text typography='caption' color='gray500'>
            {createdAt}
          </Text>
        </div>
      </div>

      <div className='mb-3'>
        <Text
          typography='body1'
          color='gray900'
          className='whitespace-pre-wrap'
        >
          {contentPreview}
        </Text>
      </div>

      {files && files.length > 0 && (
        <div className='mb-3 grid grid-cols-4 gap-2'>
          {files.map((file) => (
            <img
              key={file.fileId}
              src={file.fileUrl}
              alt={file.fileName}
              className='h-auto w-full rounded-lg object-cover'
            />
          ))}
        </div>
      )}

      {links && links.length > 0 && (
        <div className='mb-3'>
          <FeedLinkPreviewList links={links} />
        </div>
      )}

      {poll && (
        <FeedPoll poll={poll} onVote={onVote} onCancelVote={onCancelVote} />
      )}

      <div className='flex items-center gap-6'>
        <button
          onClick={() => navigate(`/post/${postId}`)}
          className='flex items-center gap-2 text-gray-500 transition-colors hover:text-blue-500'
        >
          <MessageSquare size={18} />
          {commentsCount > 0 && (
            <Text typography='caption' color='gray600'>
              {commentsCount}
            </Text>
          )}
        </button>

        <button
          onClick={onLike}
          className={`flex items-center gap-2 transition-colors ${
            liked ? 'text-red-500' : 'text-gray-500 hover:text-red-500'
          }`}
        >
          <Heart size={18} fill={liked ? 'currentColor' : 'none'} />
          {likesCount > 0 && (
            <Text typography='caption' color={liked ? 'error' : 'gray600'}>
              {likesCount}
            </Text>
          )}
        </button>

        <button
          onClick={onShare}
          className='flex items-center gap-2 text-gray-500 transition-colors hover:text-green-500'
        >
          <Share2 size={18} />
          {sharesCount > 0 && (
            <Text typography='caption' color='gray600'>
              {sharesCount}
            </Text>
          )}
        </button>
      </div>
    </div>
  );
};

export default FeedPost;
