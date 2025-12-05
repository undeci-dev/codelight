import { useRef, useEffect } from 'react';
import { Image, Smile, BarChart } from 'lucide-react';
import EmojiPicker, { EmojiClickData } from 'emoji-picker-react';

interface FeedComposerToolbarProps {
  showFileUpload: boolean;
  onToggleFileUpload: () => void;
  showEmojiPicker: boolean;
  onToggleEmojiPicker: () => void;
  showPollCreator: boolean;
  onTogglePollCreator: () => void;
  onEmojiSelect?: (emoji: string) => void;
  hasFiles?: boolean;
  hasPoll?: boolean;
}

const FeedComposerToolbar = ({
  showFileUpload,
  onToggleFileUpload,
  showEmojiPicker,
  onToggleEmojiPicker,
  showPollCreator,
  onTogglePollCreator,
  onEmojiSelect,
  hasFiles,
  hasPoll,
}: FeedComposerToolbarProps) => {
  const emojiPickerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        emojiPickerRef.current &&
        !emojiPickerRef.current.contains(event.target as Node)
      ) {
        if (showEmojiPicker) {
          onToggleEmojiPicker();
        }
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showEmojiPicker, onToggleEmojiPicker]);

  const handleEmojiClick = (emojiData: EmojiClickData) => {
    onEmojiSelect?.(emojiData.emoji);
  };

  const toolButtons = [
    {
      icon: Image,
      label: '파일',
      onClick: onToggleFileUpload,
      active: showFileUpload || hasFiles,
      disabled: hasPoll,
    },
    {
      icon: Smile,
      label: '이모지',
      onClick: onToggleEmojiPicker,
      active: showEmojiPicker,
      disabled: false,
    },
    {
      icon: BarChart,
      label: '투표',
      onClick: onTogglePollCreator,
      active: showPollCreator || hasPoll,
      disabled: hasFiles,
    },
  ];

  return (
    <div className='relative flex items-center gap-2'>
      {toolButtons.map((button) => (
        <button
          key={button.label}
          onClick={button.onClick}
          disabled={button.disabled}
          className={`rounded-full p-2 transition-colors ${
            button.active
              ? 'bg-purple-100 text-purple-600'
              : 'text-gray-600 hover:bg-gray-100'
          } ${button.disabled ? 'cursor-not-allowed opacity-50' : ''}`}
          title={button.label}
        >
          <button.icon size={20} />
        </button>
      ))}

      {showEmojiPicker && (
        <div
          ref={emojiPickerRef}
          className='absolute bottom-12 left-0 z-50 shadow-lg'
        >
          <EmojiPicker
            onEmojiClick={handleEmojiClick}
            width={320}
            height={400}
          />
        </div>
      )}
    </div>
  );
};

export default FeedComposerToolbar;
