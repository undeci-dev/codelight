import { useState, useRef, useCallback, ChangeEvent, DragEvent } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Heart,
  Share2,
  MoreVertical,
  Edit2,
  Trash2,
  X,
  Upload,
  GripVertical,
} from 'lucide-react';
import Text from '@/components/_common/Text/Text';
import SEO from '@/components/_common/SEO/SEO';
import ShareModal from '@/components/_common/Share/ShareModal';
import { getPostShareUrl } from '@/utils/share';
import FeedCommentItem from '@/components/_common/Feed/FeedCommentItem';
import FeedCommentInput from '@/components/_common/Feed/FeedCommentInput';
import FeedPoll from '@/components/_common/Feed/FeedPoll';
import FeedPollEditor from '@/components/_common/Feed/FeedPollEditor';
import FeedLinkPreviewList from '@/components/_common/Feed/FeedLinkPreviewList';
import { useComments } from '@/hooks/useComments';
import type { PollUpdateRequest } from '@/types/poll';
import type { EditableFileState } from '@/types/file';
import type { LinkPreviewResponse } from '@/types/link';
import {
  useCommentsQuery,
  useCreateCommentMutation,
  useUpdateCommentMutation,
  useDeleteCommentMutation,
  useToggleCommentLikeMutation,
} from '@/hooks/query/useCommentsQuery';
import {
  useSelectPostQuery,
  useUpdatePostMutation,
  useDeletePostMutation,
  useLikePostMutation,
  useVotePollMutation,
  useCancelVoteMutation,
} from '@/hooks/query/usePostsQuery';
import { formatAbsoluteTime } from '@/utils/feed/commentUtils';
import { fetchLinkPreview } from '@/apis/link';
import LoadingSpinner from '@/components/_common/LoadingSpinner/LoadingSpinner';

const PostDetailPage = () => {
  const { postId } = useParams<{ postId: string }>();
  const navigate = useNavigate();
  const numericPostId = Number(postId);

  const [showMenu, setShowMenu] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState('');
  const [editPoll, setEditPoll] = useState<PollUpdateRequest | null>(null);
  const [deletePoll, setDeletePoll] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [editFiles, setEditFiles] = useState<EditableFileState[]>([]);
  const [editLinks, setEditLinks] = useState<LinkPreviewResponse[]>([]);
  const [fetchedUrls, setFetchedUrls] = useState<Set<string>>(new Set());
  const [isLinkLoading, setIsLinkLoading] = useState(false);
  const [draggedIndex, setDraggedIndex] = useState<number | null>(null);
  const [dragOverIndex, setDragOverIndex] = useState<number | null>(null);
  const [showShareModal, setShowShareModal] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const MAX_FILES = 4;

  const handleGoBack = () => {
    navigate(-1);
  };

  const {
    newComment,
    setNewComment,
    replyingTo,
    replyContent,
    setReplyContent,
    handleReplyClick,
    handleCancelReply,
  } = useComments();

  const { data: post, isLoading, isError } = useSelectPostQuery(numericPostId);
  const { data: commentsData } = useCommentsQuery(numericPostId);

  const createCommentMutation = useCreateCommentMutation(numericPostId);
  const updateCommentMutation = useUpdateCommentMutation(numericPostId);
  const deleteCommentMutation = useDeleteCommentMutation(numericPostId);
  const toggleLikeMutation = useToggleCommentLikeMutation(numericPostId);

  const updatePostMutation = useUpdatePostMutation();
  const deletePostMutation = useDeletePostMutation();
  const likePostMutation = useLikePostMutation();
  const votePollMutation = useVotePollMutation();
  const cancelVoteMutation = useCancelVoteMutation();

  const handleEditStart = () => {
    if (post) {
      setEditContent(post.content);
      setEditPoll(null);
      setDeletePoll(false);
      // 기존 파일을 EditableFileState로 변환
      const existingFiles: EditableFileState[] = (post.files || []).map(
        (file) => ({
          type: 'existing' as const,
          id: `existing-${file.fileId}`,
          previewUrl: file.fileUrl,
          fileName: file.fileName,
          fileSize: file.fileSize,
          fileId: file.fileId,
        })
      );
      setEditFiles(existingFiles);
      // 기존 링크 불러오기
      const existingLinks = post.links || [];
      setEditLinks(existingLinks);
      // 기존 링크 URL을 fetchedUrls에 추가 (중복 요청 방지)
      setFetchedUrls(new Set(existingLinks.map((link) => link.url)));
      setIsLinkLoading(false);
      setIsEditing(true);
      setShowMenu(false);
    }
  };

  const handleEditCancel = () => {
    // 새로 추가한 파일의 preview URL 정리
    editFiles.forEach((file) => {
      if (file.type === 'new' && file.previewUrl) {
        URL.revokeObjectURL(file.previewUrl);
      }
    });
    setIsEditing(false);
    setEditContent('');
    setEditPoll(null);
    setDeletePoll(false);
    setEditFiles([]);
    setEditLinks([]);
    setFetchedUrls(new Set());
    setIsLinkLoading(false);
  };

  const handleEditContentChange = useCallback(
    async (content: string) => {
      setEditContent(content);

      // URL 감지 로직
      const urlRegex = /(https?:\/\/[^\s]+)/g;
      const urls = content.match(urlRegex) || [];

      if (urls.length === 0) {
        // URL이 없으면 새로 추가된 링크만 제거 (기존 링크는 유지)
        setEditLinks((prev) => prev.filter((link) => !!link.linkId));
        return;
      }

      const currentUrls = new Set(urls);
      const newUrls = urls.filter((url) => !fetchedUrls.has(url));

      // content에서 삭제된 URL의 링크 제거 (기존 링크는 linkId로 판별하여 유지)
      setEditLinks((prev) =>
        prev.filter((link) => !!link.linkId || currentUrls.has(link.url))
      );

      if (newUrls.length === 0) return;

      // 새 URL에 대해 API 호출
      setIsLinkLoading(true);

      try {
        const newLinkPreviews = await Promise.all(
          newUrls.map(async (url) => {
            try {
              return await fetchLinkPreview({ url });
            } catch {
              return {
                url,
                domain: new URL(url).hostname,
              };
            }
          })
        );

        setEditLinks((prev) => {
          const existingUrls = new Set(prev.map((link) => link.url));
          const uniqueNewLinks = newLinkPreviews.filter(
            (link) => !existingUrls.has(link.url)
          );
          return [...prev, ...uniqueNewLinks];
        });

        setFetchedUrls((prev) => new Set([...prev, ...newUrls]));
      } catch {
        // 에러 무시
      } finally {
        setIsLinkLoading(false);
      }
    },
    [fetchedUrls]
  );

  const handleLinkRemove = (linkToRemove: LinkPreviewResponse) => {
    setEditLinks(
      editLinks.filter((link) =>
        linkToRemove.linkId
          ? link.linkId !== linkToRemove.linkId
          : link.url !== linkToRemove.url
      )
    );
  };

  const handlePollChange = (poll: PollUpdateRequest) => {
    setEditPoll(poll);
  };

  const handlePollRemove = () => {
    setDeletePoll(true);
    setEditPoll(null);
  };

  const handleFileSelect = (e: ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(e.target.files || []);

    if (editFiles.length + selectedFiles.length > MAX_FILES) {
      alert(`최대 ${MAX_FILES}개의 파일만 업로드할 수 있습니다.`);
      return;
    }

    const newFiles: EditableFileState[] = selectedFiles.map((file, index) => ({
      type: 'new' as const,
      id: `new-${Date.now()}-${index}-${file.name}`,
      previewUrl: URL.createObjectURL(file),
      fileName: file.name,
      fileSize: file.size,
      file,
    }));

    setEditFiles([...editFiles, ...newFiles]);

    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const handleFileRemove = (id: string) => {
    const fileToRemove = editFiles.find((f) => f.id === id);
    // 새로 추가한 파일인 경우 preview URL 정리
    if (fileToRemove?.type === 'new' && fileToRemove.previewUrl) {
      URL.revokeObjectURL(fileToRemove.previewUrl);
    }
    setEditFiles(editFiles.filter((f) => f.id !== id));
  };

  const handleDragStart = (e: DragEvent<HTMLDivElement>, index: number) => {
    setDraggedIndex(index);
    e.dataTransfer.effectAllowed = 'move';
  };

  const handleDragOver = (e: DragEvent<HTMLDivElement>, index: number) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = 'move';
    setDragOverIndex(index);
  };

  const handleDragLeave = () => {
    setDragOverIndex(null);
  };

  const handleDrop = (e: DragEvent<HTMLDivElement>, dropIndex: number) => {
    e.preventDefault();
    if (draggedIndex === null || draggedIndex === dropIndex) {
      setDraggedIndex(null);
      setDragOverIndex(null);
      return;
    }

    const newFiles = [...editFiles];
    const [draggedItem] = newFiles.splice(draggedIndex, 1);
    newFiles.splice(dropIndex, 0, draggedItem);
    setEditFiles(newFiles);
    setDraggedIndex(null);
    setDragOverIndex(null);
  };

  const handleDragEnd = () => {
    setDraggedIndex(null);
    setDragOverIndex(null);
  };

  const handleEditSubmit = () => {
    if (!postId || !editContent.trim()) return;

    // 삭제할 기존 파일 ID 목록 계산
    const originalFileIds = (post?.files || []).map((f) => f.fileId);
    const remainingFileIds = editFiles
      .filter((f) => f.type === 'existing' && f.fileId !== undefined)
      .map((f) => f.fileId as number);
    const deleteFileIds = originalFileIds.filter(
      (id) => !remainingFileIds.includes(id)
    );

    // 삭제할 기존 링크 ID 목록 계산
    const originalLinkIds = (post?.links || [])
      .filter((l) => !!l.linkId)
      .map((l) => l.linkId as number);
    const remainingLinkIds = editLinks
      .filter((l) => !!l.linkId)
      .map((l) => l.linkId as number);
    const deleteLinkIds = originalLinkIds.filter(
      (id) => !remainingLinkIds.includes(id)
    );

    // 새로 추가된 파일 추출 (순서대로)
    const newFiles = editFiles
      .filter((f) => f.type === 'new' && f.file)
      .map((f) => f.file as File);

    // 기존 파일 순서 정보 (현재 배열 순서 기준 displayOrder 계산)
    // 기존 파일과 새 파일이 섞여있을 수 있으므로, 전체 순서에서 기존 파일의 위치를 추출
    const fileOrders = editFiles
      .map((f, index) => ({ file: f, displayOrder: index }))
      .filter(
        (item) =>
          item.file.type === 'existing' && item.file.fileId !== undefined
      )
      .map((item) => ({
        fileId: item.file.fileId as number,
        displayOrder: item.displayOrder,
      }));

    const updateData: {
      content: string;
      poll?: PollUpdateRequest;
      deleteFileIds?: number[];
      fileOrders?: { fileId: number; displayOrder: number }[];
      deleteLinkIds?: number[];
      links?: { url: string }[];
    } = {
      content: editContent,
    };

    if (deletePoll) {
      // 투표 삭제 시 빈 객체나 null을 보내는 방식은 백엔드 구현에 따라 다를 수 있음
      // 백엔드에서 poll: null을 받으면 삭제하도록 처리 필요
    } else if (editPoll) {
      updateData.poll = editPoll;
    }

    // 삭제할 파일이 있으면 추가
    if (deleteFileIds.length > 0) {
      updateData.deleteFileIds = deleteFileIds;
    }

    // 파일 순서 정보 추가 (기존 파일이 있을 경우)
    if (fileOrders.length > 0) {
      updateData.fileOrders = fileOrders;
    }

    // 삭제할 링크가 있으면 추가
    if (deleteLinkIds.length > 0) {
      updateData.deleteLinkIds = deleteLinkIds;
    }

    // 새로 추가된 링크 (linkId가 없는 것들)
    const newLinks = editLinks
      .filter((link) => !link.linkId)
      .map((link) => ({ url: link.url }));

    if (newLinks.length > 0) {
      updateData.links = newLinks;
    }

    updatePostMutation.mutate(
      {
        postId: numericPostId,
        data: updateData,
        files: newFiles.length > 0 ? newFiles : undefined,
      },
      {
        onSuccess: () => {
          // 새로 추가한 파일의 preview URL 정리
          editFiles.forEach((file) => {
            if (file.type === 'new' && file.previewUrl) {
              URL.revokeObjectURL(file.previewUrl);
            }
          });
          setIsEditing(false);
          setEditContent('');
          setEditPoll(null);
          setDeletePoll(false);
          setEditFiles([]);
          setEditLinks([]);
          setFetchedUrls(new Set());
          setIsLinkLoading(false);
        },
      }
    );
  };

  const handleDeleteClick = () => {
    setShowDeleteConfirm(true);
    setShowMenu(false);
  };

  const handleDeleteConfirm = () => {
    if (!postId) return;

    deletePostMutation.mutate(numericPostId, {
      onSuccess: () => {
        setShowDeleteConfirm(false);
        navigate(-1);
      },
    });
  };

  const handleLike = () => {
    if (!postId) return;
    likePostMutation.mutate(numericPostId);
  };

  const handleVote = (optionIds: number[]) => {
    if (!post?.poll) return;
    votePollMutation.mutate({ pollId: post.poll.pollId, optionIds });
  };

  const handleCancelVote = (pollId: number) => {
    cancelVoteMutation.mutate(pollId);
  };

  const handleCommentSubmit = () => {
    if (!postId || !newComment.trim()) return;
    createCommentMutation.mutate(
      { content: newComment.trim() },
      {
        onSuccess: () => setNewComment(''),
      }
    );
  };

  const handleReplySubmit = (parentId: number) => {
    if (!postId || !replyContent.trim()) return;
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

  if (isLoading) {
    return (
      <div className='flex min-h-[calc(100vh-60px)] items-center justify-center'>
        <Text typography='body1' color='gray500'>
          로딩 중...
        </Text>
      </div>
    );
  }

  if (isError || !post) {
    return (
      <div className='flex min-h-[calc(100vh-60px)] items-center justify-center'>
        <Text typography='body1' color='gray500'>
          게시물을 찾을 수 없습니다.
        </Text>
      </div>
    );
  }

  const comments = commentsData?.comments ?? [];
  const totalComments = commentsData?.totalCount ?? 0;

  const shareUrl = getPostShareUrl(numericPostId);
  const postPreview =
    post.content.slice(0, 100) + (post.content.length > 100 ? '...' : '');
  const postImage = post.files?.[0]?.fileUrl;

  return (
    <>
      {/* SEO 메타 태그 */}
      <SEO
        title={`${post.userName}님의 게시물`}
        description={postPreview}
        image={postImage}
        url={shareUrl}
        type='article'
        author={post.userName}
      />

      {/* 공유 모달 */}
      <ShareModal
        isOpen={showShareModal}
        onClose={() => setShowShareModal(false)}
        shareData={{
          title: `${post.userName}님의 게시물 - CodeLight`,
          text: postPreview,
          url: shareUrl,
          imageUrl: postImage,
        }}
      />

      {/* 서브 헤더 */}
      <div className='border-b border-gray-200 bg-white px-4 py-3'>
        <div className='mx-auto flex max-w-2xl items-center gap-4'>
          <button
            onClick={handleGoBack}
            className='rounded-full p-2 transition-colors hover:bg-gray-100'
          >
            <ArrowLeft size={20} />
          </button>
          <Text typography='h3' color='gray900'>
            게시물
          </Text>
        </div>
      </div>

      {/* 게시물 내용 */}
      <main className='mx-auto max-w-2xl'>
        <article className='border-b border-gray-200 bg-white p-4'>
          {/* 작성자 정보 */}
          <div className='mb-4 flex items-center justify-between'>
            <div className='flex items-center gap-3'>
              <div className='flex h-12 w-12 items-center justify-center rounded-full bg-gray-300'>
                <Text typography='body1' color='white'>
                  {post.userName.charAt(0)}
                </Text>
              </div>
              <div>
                <Text
                  typography='body1'
                  color='gray900'
                  className='font-semibold'
                >
                  {post.userName}
                </Text>
                <Text typography='caption' color='gray500'>
                  {formatAbsoluteTime(post.createdAt)}
                </Text>
              </div>
            </div>

            {/* 더보기 메뉴 (본인 글인 경우에만 표시) */}
            {post.isOwner && (
              <div className='relative'>
                <button
                  onClick={() => setShowMenu(!showMenu)}
                  className='rounded-full p-2 transition-colors hover:bg-gray-100'
                >
                  <MoreVertical size={20} className='text-gray-500' />
                </button>

                {showMenu && (
                  <div className='absolute right-0 top-10 z-10 w-32 rounded-lg border border-gray-200 bg-white py-1 shadow-lg'>
                    <button
                      onClick={handleEditStart}
                      className='flex w-full items-center gap-2 px-4 py-2 text-left text-sm text-gray-700 hover:bg-gray-100'
                    >
                      <Edit2 size={16} />
                      수정
                    </button>
                    <button
                      onClick={handleDeleteClick}
                      className='flex w-full items-center gap-2 px-4 py-2 text-left text-sm text-red-600 hover:bg-gray-100'
                    >
                      <Trash2 size={16} />
                      삭제
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* 게시물 본문 */}
          <div className='mb-4'>
            {isEditing ? (
              <div className='space-y-3'>
                <textarea
                  value={editContent}
                  onChange={(e) => handleEditContentChange(e.target.value)}
                  className='w-full resize-none rounded-lg border border-gray-300 p-3 text-lg focus:border-purple-500 focus:outline-none'
                  rows={4}
                />

                {/* 링크 로딩 표시 */}
                {isLinkLoading && (
                  <div className='flex items-center gap-2 text-gray-500'>
                    <LoadingSpinner size='sm' />
                    <span className='text-sm'>링크 정보를 가져오는 중...</span>
                  </div>
                )}

                {/* 투표 수정 */}
                {post.poll && !deletePoll && (
                  <FeedPollEditor
                    poll={post.poll}
                    onChange={handlePollChange}
                    onRemove={handlePollRemove}
                  />
                )}

                {deletePoll && (
                  <div className='rounded-lg border border-red-200 bg-red-50 p-3'>
                    <Text typography='body2' color='gray600'>
                      투표가 삭제됩니다.
                    </Text>
                    <button
                      onClick={() => setDeletePoll(false)}
                      className='mt-2 text-sm text-purple-600 hover:underline'
                    >
                      삭제 취소
                    </button>
                  </div>
                )}

                {/* 파일 수정 */}
                <div className='space-y-3'>
                  <input
                    ref={fileInputRef}
                    type='file'
                    multiple
                    accept='image/*'
                    onChange={handleFileSelect}
                    className='hidden'
                  />

                  {editFiles.length < MAX_FILES && (
                    <button
                      type='button'
                      onClick={() => fileInputRef.current?.click()}
                      className='flex items-center gap-2 rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm text-gray-700 hover:bg-gray-50'
                    >
                      <Upload size={16} />
                      이미지 추가
                    </button>
                  )}

                  {editFiles.length > 0 && (
                    <div className='grid grid-cols-2 gap-3'>
                      {editFiles.map((file, index) => (
                        <div
                          key={file.id}
                          draggable
                          onDragStart={(e) => handleDragStart(e, index)}
                          onDragOver={(e) => handleDragOver(e, index)}
                          onDragLeave={handleDragLeave}
                          onDrop={(e) => handleDrop(e, index)}
                          onDragEnd={handleDragEnd}
                          className={`relative rounded-lg border-2 p-2 transition-all ${
                            draggedIndex === index
                              ? 'border-purple-400 opacity-50'
                              : dragOverIndex === index
                                ? 'border-purple-500 bg-purple-50'
                                : 'border-gray-200'
                          } cursor-grab active:cursor-grabbing`}
                        >
                          {/* 드래그 핸들 */}
                          <div className='absolute left-1 top-1 z-10 rounded bg-gray-900 bg-opacity-60 p-0.5 text-white'>
                            <GripVertical size={14} />
                          </div>
                          {/* 순서 번호 */}
                          <div className='absolute bottom-1 left-1 z-10 flex h-5 w-5 items-center justify-center rounded-full bg-purple-600 text-xs font-medium text-white'>
                            {index + 1}
                          </div>
                          <button
                            type='button'
                            onClick={() => handleFileRemove(file.id)}
                            className='absolute right-1 top-1 z-10 rounded-full bg-gray-900 bg-opacity-70 p-1 text-white hover:bg-opacity-90'
                          >
                            <X size={14} />
                          </button>
                          <img
                            src={file.previewUrl}
                            alt={file.fileName}
                            className='h-28 w-full rounded object-cover'
                          />
                          {file.type === 'existing' && (
                            <div className='absolute bottom-1 right-1 rounded bg-gray-900 bg-opacity-60 px-1.5 py-0.5 text-xs text-white'>
                              기존
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  )}
                  {editFiles.length > 1 && (
                    <Text typography='caption' color='gray500'>
                      드래그하여 이미지 순서를 변경할 수 있습니다
                    </Text>
                  )}
                </div>

                {/* 링크 수정 */}
                {editLinks.length > 0 && (
                  <div className='space-y-2'>
                    <Text
                      typography='body2'
                      color='gray700'
                      className='font-medium'
                    >
                      링크
                    </Text>
                    {editLinks.map((link) => (
                      <div
                        key={link.linkId ?? link.url}
                        className='flex items-center gap-2 rounded-lg border border-gray-200 p-2'
                      >
                        {link.image && (
                          <img
                            src={link.image}
                            alt={link.title || '링크 미리보기'}
                            className='h-12 w-12 rounded object-cover'
                          />
                        )}
                        <div className='min-w-0 flex-1'>
                          <Text
                            typography='body2'
                            color='gray900'
                            className='truncate font-medium'
                          >
                            {link.title || link.url}
                          </Text>
                          <Text
                            typography='caption'
                            color='gray500'
                            className='truncate'
                          >
                            {link.domain || link.url}
                          </Text>
                        </div>
                        <button
                          type='button'
                          onClick={() => handleLinkRemove(link)}
                          className='flex-shrink-0 rounded-full p-1 text-gray-400 hover:bg-gray-100 hover:text-gray-600'
                        >
                          <X size={16} />
                        </button>
                      </div>
                    ))}
                  </div>
                )}

                <div className='flex justify-end gap-2'>
                  <button
                    onClick={handleEditCancel}
                    disabled={updatePostMutation.isPending}
                    className='rounded-lg px-4 py-2 text-sm text-gray-600 hover:bg-gray-100'
                  >
                    취소
                  </button>
                  <button
                    onClick={handleEditSubmit}
                    disabled={
                      updatePostMutation.isPending || !editContent.trim()
                    }
                    className='rounded-lg bg-purple-600 px-4 py-2 text-sm text-white hover:bg-purple-700 disabled:cursor-not-allowed disabled:opacity-50'
                  >
                    {updatePostMutation.isPending ? '수정 중...' : '수정'}
                  </button>
                </div>
              </div>
            ) : (
              <Text
                typography='body1'
                color='gray900'
                className='whitespace-pre-wrap text-lg'
              >
                {post.content}
              </Text>
            )}
          </div>

          {/* 첨부 이미지 */}
          {!isEditing && post.files && post.files.length > 0 && (
            <div className='mb-4 grid grid-cols-2 gap-2'>
              {post.files.map((file) => (
                <img
                  key={file.fileId}
                  src={file.fileUrl}
                  alt={file.fileName}
                  className='h-auto w-full rounded-lg object-cover'
                />
              ))}
            </div>
          )}

          {/* 링크 미리보기 */}
          {!isEditing && post.links && post.links.length > 0 && (
            <div className='mb-4'>
              <FeedLinkPreviewList links={post.links} />
            </div>
          )}

          {/* 투표 (읽기 모드) */}
          {!isEditing && post.poll && (
            <FeedPoll
              poll={post.poll}
              onVote={handleVote}
              onCancelVote={handleCancelVote}
            />
          )}

          {/* 액션 버튼 */}
          <div className='flex items-center gap-6 border-t border-gray-100 pt-4'>
            <button
              onClick={handleLike}
              className={`flex items-center gap-2 transition-colors ${
                post.liked ? 'text-red-500' : 'text-gray-500 hover:text-red-500'
              }`}
            >
              <Heart size={20} fill={post.liked ? 'currentColor' : 'none'} />
              {post.likesCount > 0 && (
                <Text
                  typography='body2'
                  color={post.liked ? 'error' : 'gray600'}
                >
                  {post.likesCount}
                </Text>
              )}
            </button>

            <button
              onClick={() => setShowShareModal(true)}
              className='flex items-center gap-2 text-gray-500 transition-colors hover:text-green-500'
            >
              <Share2 size={20} />
            </button>
          </div>
        </article>

        {/* 댓글 섹션 */}
        <section className='bg-white'>
          <div className='border-b border-gray-200 p-4'>
            <Text typography='body1' color='gray900' className='font-semibold'>
              댓글 {totalComments}개
            </Text>
          </div>

          {/* 댓글 입력 */}
          <div className='border-b border-gray-200 p-4'>
            <FeedCommentInput
              value={newComment}
              onChange={setNewComment}
              onSubmit={handleCommentSubmit}
              placeholder='댓글을 입력하세요...'
            />
          </div>

          {/* 댓글 목록 */}
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
        </section>
      </main>

      {/* 삭제 확인 모달 */}
      {showDeleteConfirm && (
        <div className='fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50'>
          <div className='mx-4 w-full max-w-sm rounded-lg bg-white p-6'>
            <Text typography='h3' color='gray900' className='mb-2'>
              게시물 삭제
            </Text>
            <Text typography='body2' color='gray600' className='mb-6'>
              이 게시물을 삭제하시겠습니까? 삭제된 게시물은 복구할 수 없습니다.
            </Text>
            <div className='flex justify-end gap-2'>
              <button
                onClick={() => setShowDeleteConfirm(false)}
                disabled={deletePostMutation.isPending}
                className='rounded-lg px-4 py-2 text-sm text-gray-600 hover:bg-gray-100'
              >
                취소
              </button>
              <button
                onClick={handleDeleteConfirm}
                disabled={deletePostMutation.isPending}
                className='rounded-lg bg-red-600 px-4 py-2 text-sm text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50'
              >
                {deletePostMutation.isPending ? '삭제 중...' : '삭제'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default PostDetailPage;
