import { Send } from 'lucide-react';
import Button from '@/components/_common/Button/Button';

interface FeedCommentInputProps {
  value: string;
  onChange: (value: string) => void;
  onSubmit: () => void;
  placeholder?: string;
  autoFocus?: boolean;
  size?: 'sm' | 'md';
}

const FeedCommentInput = ({
  value,
  onChange,
  onSubmit,
  placeholder = '댓글을 입력하세요...',
  autoFocus = false,
  size = 'md',
}: FeedCommentInputProps) => {
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && !e.shiftKey && value.trim()) {
      e.preventDefault();
      onSubmit();
    }
  };

  const inputClasses =
    size === 'sm' ? 'px-3 py-2 text-sm' : 'px-4 py-3 text-base';

  return (
    <div className='flex gap-2'>
      <input
        type='text'
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyPress={handleKeyPress}
        placeholder={placeholder}
        autoFocus={autoFocus}
        className={`flex-1 rounded-lg border border-gray-300 focus:border-purple-500 focus:outline-none focus:ring-2 focus:ring-purple-200 ${inputClasses}`}
      />
      <Button
        label='전송'
        variant='primary'
        size={size}
        onClick={onSubmit}
        disabled={!value.trim()}
        Icon={Send}
        iconPosition='start'
      />
    </div>
  );
};

export default FeedCommentInput;
