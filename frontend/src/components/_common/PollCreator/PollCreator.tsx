import { useState } from 'react';
import { X, Plus } from 'lucide-react';
import Text from '@/components/_common/Text/Text';
import type { PollCreateRequest, PollOptionCreateRequest } from '@/types/poll';

interface PollCreatorProps {
  onCreatePoll: (poll: PollCreateRequest) => void;
  onClose: () => void;
}

const PollCreator = ({ onCreatePoll, onClose }: PollCreatorProps) => {
  const [question, setQuestion] = useState('');
  const [options, setOptions] = useState(['', '']);
  const [multipleChoice, setMultipleChoice] = useState(false);

  const handleAddOption = () => {
    if (options.length < 4) {
      setOptions([...options, '']);
    }
  };

  const handleRemoveOption = (index: number) => {
    if (options.length > 2) {
      setOptions(options.filter((_, i) => i !== index));
    }
  };

  const handleOptionChange = (index: number, value: string) => {
    const newOptions = [...options];
    newOptions[index] = value;
    setOptions(newOptions);
  };

  const handleSubmit = () => {
    if (!question.trim()) {
      alert('질문을 입력해주세요.');
      return;
    }

    const validOptions = options.filter((opt) => opt.trim());
    if (validOptions.length < 2) {
      alert('최소 2개의 선택지를 입력해주세요.');
      return;
    }

    const pollOptions: PollOptionCreateRequest[] = validOptions.map(
      (optionText, index) => ({
        optionText: optionText.trim(),
        displayOrder: index,
      })
    );

    onCreatePoll({
      question: question.trim(),
      options: pollOptions,
      multipleChoice,
    });
  };

  const canSubmit =
    question.trim() && options.filter((opt) => opt.trim()).length >= 2;

  return (
    <div className='rounded-lg border border-gray-200 bg-gray-50 p-3'>
      <div className='mb-3 flex items-center justify-between'>
        <Text typography='body2' color='gray700' className='font-medium'>
          투표 만들기
        </Text>
        <button
          onClick={onClose}
          className='rounded-full p-0.5 hover:bg-gray-200'
        >
          <X size={16} className='text-gray-500' />
        </button>
      </div>

      <div className='space-y-2'>
        <input
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          placeholder='질문을 입력하세요'
          maxLength={100}
          className='w-full rounded-md border border-gray-300 px-3 py-2 text-sm focus:border-purple-500 focus:outline-none'
        />

        <div className='space-y-1.5'>
          {options.map((option, index) => (
            <div key={index} className='flex items-center gap-1.5'>
              <input
                value={option}
                onChange={(e) => handleOptionChange(index, e.target.value)}
                placeholder={`선택지 ${index + 1}`}
                maxLength={50}
                className='flex-1 rounded-md border border-gray-300 px-3 py-1.5 text-sm focus:border-purple-500 focus:outline-none'
              />
              {options.length > 2 && (
                <button
                  onClick={() => handleRemoveOption(index)}
                  className='rounded-full p-1 hover:bg-gray-200'
                >
                  <X size={14} className='text-gray-500' />
                </button>
              )}
            </div>
          ))}
        </div>

        {options.length < 4 && (
          <button
            onClick={handleAddOption}
            className='flex items-center gap-1 text-sm text-purple-600 hover:text-purple-700'
          >
            <Plus size={14} />
            선택지 추가
          </button>
        )}

        <label className='flex items-center gap-1.5'>
          <input
            type='checkbox'
            checked={multipleChoice}
            onChange={(e) => setMultipleChoice(e.target.checked)}
            className='h-3.5 w-3.5 rounded border-gray-300'
          />
          <span className='text-xs text-gray-600'>복수 선택 허용</span>
        </label>

        <div className='flex justify-end gap-2 pt-1'>
          <button
            onClick={onClose}
            className='rounded-md px-3 py-1.5 text-xs text-gray-600 hover:bg-gray-200'
          >
            취소
          </button>
          <button
            onClick={handleSubmit}
            disabled={!canSubmit}
            className='rounded-md bg-purple-600 px-3 py-1.5 text-xs text-white hover:bg-purple-700 disabled:cursor-not-allowed disabled:opacity-50'
          >
            생성
          </button>
        </div>
      </div>
    </div>
  );
};

export default PollCreator;
