import { useState } from 'react';
import { X, Plus } from 'lucide-react';
import Input from '@/components/_common/Input/Input';
import Text from '@/components/_common/Text/Text';
import type {
  PollResponse,
  PollUpdateRequest,
  PollOptionCreateRequest,
} from '@/types/poll';

interface FeedPollEditorProps {
  poll: PollResponse;
  onChange: (poll: PollUpdateRequest) => void;
  onRemove: () => void;
}

const FeedPollEditor = ({ poll, onChange, onRemove }: FeedPollEditorProps) => {
  const [question, setQuestion] = useState(poll.question);
  const [options, setOptions] = useState<string[]>(
    poll.options
      .sort((a, b) => a.displayOrder - b.displayOrder)
      .map((opt) => opt.optionText)
  );
  const [multipleChoice, setMultipleChoice] = useState(poll.multipleChoice);

  const hasVotes = poll.totalVotes > 0;

  const handleQuestionChange = (value: string) => {
    setQuestion(value);
    emitChange(value, options, multipleChoice);
  };

  const handleOptionChange = (index: number, value: string) => {
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);
    emitChange(question, newOptions, multipleChoice);
  };

  const handleAddOption = () => {
    if (options.length < 4) {
      const newOptions = [...options, ''];
      setOptions(newOptions);
      emitChange(question, newOptions, multipleChoice);
    }
  };

  const handleRemoveOption = (index: number) => {
    if (options.length > 2) {
      const newOptions = options.filter((_, i) => i !== index);
      setOptions(newOptions);
      emitChange(question, newOptions, multipleChoice);
    }
  };

  const handleMultipleChoiceChange = (checked: boolean) => {
    setMultipleChoice(checked);
    emitChange(question, options, checked);
  };

  const emitChange = (q: string, opts: string[], multi: boolean) => {
    const pollOptions: PollOptionCreateRequest[] = opts
      .filter((opt) => opt.trim())
      .map((optionText, index) => ({
        optionText: optionText.trim(),
        displayOrder: index,
      }));

    onChange({
      question: q.trim(),
      options: pollOptions,
      multipleChoice: multi,
    });
  };

  return (
    <div className='mt-3 rounded-lg border border-gray-200 bg-gray-50 p-4'>
      <div className='mb-4 flex items-center justify-between'>
        <Text typography='body1' color='gray900' className='font-semibold'>
          투표 수정
        </Text>
        <button
          onClick={onRemove}
          className='rounded-full p-1 text-gray-500 hover:bg-gray-200'
          title='투표 삭제'
        >
          <X size={18} />
        </button>
      </div>

      {hasVotes && (
        <div className='mb-3 rounded-lg bg-yellow-50 p-3'>
          <Text typography='caption' color='gray600'>
            이미 {poll.totalVotes}명이 투표했습니다. 질문만 수정할 수 있습니다.
          </Text>
        </div>
      )}

      <div className='space-y-4'>
        <Input
          value={question}
          onChange={(e) => handleQuestionChange(e.target.value)}
          placeholder='질문을 입력하세요'
          maxLength={100}
        />

        <div className='space-y-2'>
          {options.map((option, index) => (
            <div key={index} className='flex gap-2'>
              <Input
                value={option}
                onChange={(e) => handleOptionChange(index, e.target.value)}
                placeholder={`선택지 ${index + 1}`}
                maxLength={50}
                disabled={hasVotes}
              />
              {options.length > 2 && !hasVotes && (
                <button
                  onClick={() => handleRemoveOption(index)}
                  className='rounded-full p-2 hover:bg-gray-200'
                >
                  <X size={16} className='text-gray-600' />
                </button>
              )}
            </div>
          ))}
        </div>

        {options.length < 4 && !hasVotes && (
          <button
            onClick={handleAddOption}
            className='flex items-center gap-1 text-sm text-purple-600 hover:text-purple-700'
          >
            <Plus size={16} />
            선택지 추가
          </button>
        )}

        <label
          className={`flex items-center gap-2 ${hasVotes ? 'cursor-not-allowed opacity-50' : ''}`}
        >
          <input
            type='checkbox'
            checked={multipleChoice}
            onChange={(e) => handleMultipleChoiceChange(e.target.checked)}
            disabled={hasVotes}
            className='h-4 w-4 rounded border-gray-300 disabled:cursor-not-allowed'
          />
          <Text typography='body2' color={hasVotes ? 'gray500' : 'gray700'}>
            복수 선택 허용
          </Text>
        </label>
      </div>
    </div>
  );
};

export default FeedPollEditor;
