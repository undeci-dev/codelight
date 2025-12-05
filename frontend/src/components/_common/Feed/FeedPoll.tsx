import { Check } from 'lucide-react';
import Text from '@/components/_common/Text/Text';
import type { PollResponse } from '@/types/poll';

interface FeedPollProps {
  poll: PollResponse;
  onVote?: (optionIds: number[]) => void;
  onCancelVote?: (pollId: number) => void;
}

const FeedPoll = ({ poll, onVote, onCancelVote }: FeedPollProps) => {
  const selectedOptions = poll.votedOptionIds || [];

  const sortedOptions = [...poll.options].sort(
    (a, b) => a.displayOrder - b.displayOrder
  );

  const handleOptionClick = (optionId: number) => {
    if (poll.hasVoted || poll.isExpired) return;

    const newSelected = poll.multipleChoice
      ? selectedOptions.includes(optionId)
        ? selectedOptions.filter((id) => id !== optionId)
        : [...selectedOptions, optionId]
      : [optionId];

    if (onVote && newSelected.length > 0) {
      onVote(newSelected);
    }
  };

  const handleCancelVote = (e: React.MouseEvent) => {
    e.stopPropagation();
    if (onCancelVote) {
      onCancelVote(poll.pollId);
    }
  };

  const showResults = poll.hasVoted || poll.isExpired;

  return (
    <div className='mt-3 rounded-lg border border-gray-200 bg-gray-50 p-4'>
      <Text typography='body1' color='gray900' className='mb-3 font-semibold'>
        {poll.question}
      </Text>

      <div className='space-y-2'>
        {sortedOptions.map((option) => {
          const isSelected = selectedOptions.includes(option.optionId);
          const percentage = option.percentage || 0;

          return (
            <button
              key={option.optionId}
              onClick={(e) => {
                e.stopPropagation();
                handleOptionClick(option.optionId);
              }}
              disabled={poll.hasVoted || poll.isExpired}
              className={`relative w-full overflow-hidden rounded-lg border px-4 py-3 text-left transition-all ${
                isSelected
                  ? 'border-blue-500 bg-blue-50'
                  : 'border-gray-300 bg-white hover:border-gray-400'
              } ${poll.hasVoted || poll.isExpired ? 'cursor-default' : 'cursor-pointer'}`}
            >
              {showResults && (
                <div
                  className='absolute left-0 top-0 h-full bg-blue-100 transition-all'
                  style={{ width: `${percentage}%` }}
                />
              )}

              <div className='relative flex items-center justify-between'>
                <div className='flex items-center gap-2'>
                  {isSelected && <Check size={16} className='text-blue-500' />}
                  <Text typography='body2' color='gray700'>
                    {option.optionText}
                  </Text>
                </div>

                {showResults && (
                  <Text typography='body2' color='gray600'>
                    {percentage.toFixed(0)}%
                  </Text>
                )}
              </div>
            </button>
          );
        })}
      </div>

      <div className='mt-3 flex items-center justify-between'>
        <div className='flex items-center gap-2'>
          <Text typography='caption' color='gray500'>
            {poll.totalVotes}명 투표
            {poll.multipleChoice && ' · 복수 선택'}
          </Text>
          {poll.hasVoted && !poll.isExpired && onCancelVote && (
            <button
              onClick={handleCancelVote}
              className='text-xs text-gray-500 hover:text-gray-700 hover:underline'
            >
              투표 취소
            </button>
          )}
        </div>

        {poll.isExpired ? (
          <Text typography='caption' color='gray500'>
            투표 종료
          </Text>
        ) : poll.endsAt ? (
          <Text typography='caption' color='gray500'>
            {new Date(poll.endsAt).toLocaleDateString()} 마감
          </Text>
        ) : null}
      </div>
    </div>
  );
};

export default FeedPoll;
