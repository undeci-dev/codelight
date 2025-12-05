import { useState } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import Feed from '@/components/_common/Feed/Feed';
import {
  useCreatePostMutation,
  useSelectPostsQuery,
  useLikePostMutation,
  useVotePollMutation,
  useCancelVoteMutation,
} from '@/hooks/query/usePostsQuery';
import { formatRelativeTime } from '@/utils/feed/commentUtils';
import type { PollCreateRequest } from '@/types/poll';
import type { LinkPreviewResponse } from '@/types/link';

const MainPage = () => {
  const [refreshing, setRefreshing] = useState(false);
  const queryClient = useQueryClient();

  const { data, isLoading, isError } = useSelectPostsQuery();
  const { mutate: createPost } = useCreatePostMutation();
  const { mutate: likePost } = useLikePostMutation();
  const { mutate: votePoll } = useVotePollMutation();
  const { mutate: cancelVote } = useCancelVoteMutation();

  const posts = data?.posts ?? [];

  const handlePostSubmit = (
    content: string,
    links: LinkPreviewResponse[],
    files: File[],
    poll?: PollCreateRequest
  ) => {
    createPost({
      postData: {
        content,
        poll,
        links: links.map((link) => ({ url: link.url })),
      },
      files: files.length > 0 ? files : undefined,
    });
  };

  const handleLike = (postId: number) => {
    likePost(postId);
  };

  const handleVote = (pollId: number, optionIds: number[]) => {
    votePoll({ pollId, optionIds });
  };

  const handleCancelVote = (pollId: number) => {
    cancelVote(pollId);
  };

  const handleRefresh = () => {
    setRefreshing(true);
    queryClient
      .invalidateQueries({ queryKey: ['posts', 'list'] })
      .finally(() => {
        setRefreshing(false);
      });
  };

  if (isLoading) {
    return (
      <Feed>
        <Feed.Header
          title='홈'
          onRefresh={handleRefresh}
          refreshing={refreshing}
        />
        <div className='flex items-center justify-center py-12'>
          <span className='text-gray-500'>로딩 중...</span>
        </div>
      </Feed>
    );
  }

  if (isError) {
    return (
      <Feed>
        <Feed.Header
          title='홈'
          onRefresh={handleRefresh}
          refreshing={refreshing}
        />
        <div className='flex items-center justify-center py-12'>
          <span className='text-red-500'>
            게시물을 불러오는데 실패했습니다.
          </span>
        </div>
      </Feed>
    );
  }

  return (
    <Feed>
      <Feed.Header
        title='홈'
        onRefresh={handleRefresh}
        refreshing={refreshing}
      />

      <Feed.Composer
        onPost={handlePostSubmit}
        placeholder='무슨 일이 일어나고 있나요?'
      />

      <Feed.List
        empty={posts.length === 0}
        emptyMessage='첫 게시물을 작성해보세요!'
      >
        {posts.map((post) => (
          <Feed.Post
            key={post.postId}
            postId={post.postId}
            userName={post.userName}
            contentPreview={post.contentPreview}
            createdAt={formatRelativeTime(post.createdAt)}
            likesCount={post.likesCount}
            commentsCount={post.commentsCount}
            liked={post.liked}
            onLike={() => handleLike(post.postId)}
            onShare={() => console.log('공유', post.postId)}
            files={post.files}
            poll={post.poll}
            links={post.links}
            onVote={(optionIds) =>
              post.poll && handleVote(post.poll.pollId, optionIds)
            }
            onCancelVote={handleCancelVote}
          />
        ))}
      </Feed.List>
    </Feed>
  );
};

export default MainPage;
