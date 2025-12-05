import { X } from 'lucide-react';
import type { PollCreateRequest } from '@/types/poll';
import Text from '@/components/_common/Text/Text';

interface PollPreviewProps {
  poll: PollCreateRequest;
  onRemove?: () => void;
}

const FeedPollPreview = ({ poll, onRemove }: PollPreviewProps) => {
  const sortedOptions = [...poll.options].sort(
    (a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0)
  );

  return (
    <div className='relative mt-3 rounded-lg border border-gray-200 bg-white p-4'>
      {onRemove && (
        <button
          onClick={onRemove}
          className='absolute right-2 top-2 rounded-full p-1 hover:bg-gray-100'
        >
          <X size={16} className='text-gray-600' />
        </button>
      )}

      <Text typography='body1' color='gray900' className='mb-3 font-semibold'>
        {poll.question}
      </Text>

      <div className='space-y-2'>
        {sortedOptions.map((option, index) => (
          <div
            key={index}
            className='rounded-lg border border-gray-300 px-4 py-2'
          >
            <Text typography='body2' color='gray700'>
              {option.optionText}
            </Text>
          </div>
        ))}
      </div>

      <div className='mt-3 flex items-center gap-2'>
        {poll.multipleChoice && (
          <Text typography='caption' color='gray500'>
            복수 선택 가능
          </Text>
        )}
      </div>
    </div>
  );
};

export default FeedPollPreview;
