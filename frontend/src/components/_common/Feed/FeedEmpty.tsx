import { HTMLAttributes, ReactNode } from 'react';
import { MessageSquare } from 'lucide-react';
import Text from '@/components/_common/Text/Text';

interface FeedEmptyProps extends HTMLAttributes<HTMLDivElement> {
  message?: string;
  icon?: ReactNode;
}

const FeedEmpty = ({
  message = '게시물이 없습니다.',
  icon,
  className = '',
  ...rest
}: FeedEmptyProps) => {
  return (
    <div
      className={`flex flex-col items-center justify-center py-16 ${className}`}
      {...rest}
    >
      {icon || <MessageSquare size={48} className='mb-4 text-gray-300' />}
      <Text typography='body1' color='gray500'>
        {message}
      </Text>
    </div>
  );
};

export default FeedEmpty;
