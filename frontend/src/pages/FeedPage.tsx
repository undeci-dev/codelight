import { useState, useEffect, useRef, useCallback } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { Search, X } from 'lucide-react';
import Feed from '@/components/_common/Feed/Feed';
import ShareModal from '@/components/_common/Share/ShareModal';
import {
  useCreatePostMutation,
  useInfinitePostsQuery,
  useLikePostMutation,
  useVotePollMutation,
  useCancelVoteMutation,
} from '@/hooks/query/usePostsQuery';
import { formatRelativeTime } from '@/utils/feed/commentUtils';
import { getPostShareUrl } from '@/utils/share';
import type { PollCreateRequest } from '@/types/poll';
import type { LinkPreviewResponse } from '@/types/link';
import type { PostSummaryResponse } from '@/types/post';

const FeedPage = () => {
  const [refreshing, setRefreshing] = useState(false);
  const [sharePost, setSharePost] = useState<PostSummaryResponse | null>(null);
  const [searchInput, setSearchInput] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const queryClient = useQueryClient();
  const loadMoreRef = useRef<HTMLDivElement>(null);

  const {
    data,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfinitePostsQuery(searchKeyword);
  const { mutate: createPost } = useCreatePostMutation();
  const { mutate: likePost } = useLikePostMutation();
  const { mutate: votePoll } = useVotePollMutation();
  const { mutate: cancelVote } = useCancelVoteMutation();

  // 모든 페이지의 게시글을 하나의 배열로 합침
  const posts = data?.pages.flatMap((page) => page.posts) ?? [];

  const handleObserver = useCallback(
    (entries: IntersectionObserverEntry[]) => {
      const [target] = entries;
      if (target.isIntersecting && hasNextPage && !isFetchingNextPage) {
        fetchNextPage();
      }
    },
    [fetchNextPage, hasNextPage, isFetchingNextPage]
  );

  useEffect(() => {
    const element = loadMoreRef.current;
    if (!element) return;

    const observer = new IntersectionObserver(handleObserver, {
      threshold: 0.1,
    });

    observer.observe(element);

    return () => observer.disconnect();
  }, [handleObserver]);

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

  const handleShare = (post: PostSummaryResponse) => {
    setSharePost(post);
  };

  const handleRefresh = () => {
    setRefreshing(true);
    queryClient
      .invalidateQueries({ queryKey: ['posts', 'list', searchKeyword] })
      .finally(() => {
        setRefreshing(false);
      });
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setSearchKeyword(searchInput.trim());
  };

  const handleClearSearch = () => {
    setSearchInput('');
    setSearchKeyword('');
  };

  if (isLoading) {
    return (
      <Feed>
        <Feed.Header
          title='피드'
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
          title='피드'
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
    <>
      {/* 공유 모달 */}
      {sharePost && (
        <ShareModal
          isOpen={!!sharePost}
          onClose={() => setSharePost(null)}
          shareData={{
            title: `${sharePost.userName}님의 게시물 - CodeLight`,
            text: sharePost.contentPreview,
            url: getPostShareUrl(sharePost.postId),
            imageUrl: sharePost.files?.[0]?.fileUrl,
          }}
        />
      )}

      <Feed>
        <Feed.Header
          title='피드'
          onRefresh={handleRefresh}
          refreshing={refreshing}
        />

        {/* 검색 UI */}
        <div className='border-b border-gray-200 bg-white px-4 py-3'>
          <form onSubmit={handleSearch} className='relative'>
            <div className='relative'>
              <Search
                size={18}
                className='absolute left-3 top-1/2 -translate-y-1/2 text-gray-400'
              />
              <input
                type='text'
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
                placeholder='게시글 검색...'
                className='w-full rounded-full border border-gray-300 bg-gray-50 py-2 pl-10 pr-10 text-sm transition-colors focus:border-purple-500 focus:bg-white focus:outline-none'
              />
              {searchInput && (
                <button
                  type='button'
                  onClick={handleClearSearch}
                  className='absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600'
                >
                  <X size={16} />
                </button>
              )}
            </div>
          </form>
          {searchKeyword && (
            <div className='mt-2 flex items-center gap-2'>
              <span className='text-sm text-gray-500'>
                "{searchKeyword}" 검색 결과
              </span>
              <button
                onClick={handleClearSearch}
                className='text-sm text-purple-600 hover:underline'
              >
                검색 초기화
              </button>
            </div>
          )}
        </div>

        <Feed.Composer
          onPost={handlePostSubmit}
          placeholder='무슨 일이 일어나고 있나요?'
        />

        <Feed.List
          empty={posts.length === 0}
          emptyMessage={
            searchKeyword
              ? `"${searchKeyword}"에 대한 검색 결과가 없습니다.`
              : '첫 게시물을 작성해보세요!'
          }
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
              onShare={() => handleShare(post)}
              files={post.files}
              poll={post.poll}
              links={post.links}
              onVote={(optionIds) =>
                post.poll && handleVote(post.poll.pollId, optionIds)
              }
              onCancelVote={handleCancelVote}
            />
          ))}

          {/* 무한 스크롤 감지 영역 */}
          <div ref={loadMoreRef} className='h-10'>
            {isFetchingNextPage && (
              <div className='flex items-center justify-center py-4'>
                <span className='text-gray-500'>불러오는 중...</span>
              </div>
            )}
          </div>
        </Feed.List>
      </Feed>
    </>
  );
};

export default FeedPage;
