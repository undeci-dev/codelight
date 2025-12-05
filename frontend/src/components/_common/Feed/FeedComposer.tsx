import { HTMLAttributes } from 'react';
import { Send } from 'lucide-react';
import Button from '@/components/_common/Button/Button';
import Avatar from '@/components/_common/Avatar/Avatar';
import TextArea from '@/components/_common/TextArea/TextArea';
import CharacterCounter from '@/components/_common/CharacterCounter/CharacterCounter';
import LoadingSpinner from '@/components/_common/LoadingSpinner/LoadingSpinner';
import FileUpload from '@/components/_common/FileUpload/FileUpload';
import PollCreator from '@/components/_common/PollCreator/PollCreator';
import FeedComposerToolbar from './FeedComposerToolbar';
import FeedLinkPreviewList from './FeedLinkPreviewList';
import FeedPollPreview from './FeedPollPreview';
import usePostComposerStore from '@/store/usePostComposerStore';
import type { PollCreateRequest } from '@/types/poll';
import type { LinkPreviewResponse } from '@/types/link';

interface FeedComposerProps
  extends Omit<HTMLAttributes<HTMLDivElement>, 'onSubmit'> {
  onPost?: (
    content: string,
    links: LinkPreviewResponse[],
    files: File[],
    poll?: PollCreateRequest
  ) => void;
  placeholder?: string;
  maxLength?: number;
  userName?: string;
  userAvatar?: string;
}

const FeedComposer = ({
  onPost,
  placeholder = '무슨 일이 일어나고 있나요?',
  maxLength = 280,
  userName = 'User',
  userAvatar,
  className = '',
  ...rest
}: FeedComposerProps) => {
  const {
    content,
    detectedLinks,
    isLoading,
    showEmojiPicker,
    showFileUpload,
    showPollCreator,
    uploadedFiles,
    poll,
    setUploadedFiles,
    handleContentChange,
    removeLinkPreview,
    handleEmojiSelect,
    setPoll,
    toggleFileUpload,
    toggleEmojiPicker,
    togglePollCreator,
    resetForm,
    canSubmit,
  } = usePostComposerStore();

  const handleSubmit = () => {
    if (!canSubmit(maxLength)) return;

    if (onPost) {
      const files = uploadedFiles.map((f) => f.file);
      onPost(content, detectedLinks, files, poll ?? undefined);
    }
    resetForm();
  };

  return (
    <div
      className={`border-b border-gray-200 bg-white p-6 ${className}`}
      {...rest}
    >
      <div className='flex gap-4'>
        <Avatar name={userName} src={userAvatar} size='lg' />

        <div className='flex-1'>
          <TextArea
            value={content}
            onChange={(e) => handleContentChange(e.target.value)}
            placeholder={placeholder}
            maxLength={maxLength}
            rows={3}
            className='border-0 p-0'
          />

          <FeedLinkPreviewList
            links={detectedLinks}
            onRemoveLink={removeLinkPreview}
          />

          {showFileUpload && (
            <div className='mt-3'>
              <FileUpload
                files={uploadedFiles}
                onFilesChange={setUploadedFiles}
              />
            </div>
          )}

          {poll && (
            <FeedPollPreview poll={poll} onRemove={() => setPoll(null)} />
          )}

          {showPollCreator && (
            <div className='mt-3'>
              <PollCreator
                onCreatePoll={(pollData) => {
                  setPoll(pollData);
                  togglePollCreator();
                }}
                onClose={togglePollCreator}
              />
            </div>
          )}

          {isLoading && (
            <div className='mt-3 flex items-center gap-2 text-gray-500'>
              <LoadingSpinner size='sm' />
              <span className='text-sm'>링크 정보를 가져오는 중...</span>
            </div>
          )}

          <div className='mt-4 flex items-center justify-between'>
            <FeedComposerToolbar
              showFileUpload={showFileUpload}
              onToggleFileUpload={toggleFileUpload}
              showEmojiPicker={showEmojiPicker}
              onToggleEmojiPicker={toggleEmojiPicker}
              showPollCreator={showPollCreator}
              onTogglePollCreator={togglePollCreator}
              onEmojiSelect={handleEmojiSelect}
              hasFiles={uploadedFiles.length > 0}
              hasPoll={!!poll}
            />

            <div className='flex items-center gap-3'>
              <CharacterCounter current={content.length} max={maxLength} />

              <Button
                label='게시'
                variant='primary'
                size='sm'
                onClick={handleSubmit}
                disabled={!canSubmit(maxLength)}
                Icon={Send}
                iconPosition='start'
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FeedComposer;
