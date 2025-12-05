import { HTMLAttributes, ReactNode } from 'react';
import { RefreshCw } from 'lucide-react';
import Text from '@/components/_common/Text/Text';

interface FeedListProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode;
  loading?: boolean;
  empty?: boolean;
  emptyMessage?: string;
}

const FeedList = ({
  children,
  loading = false,
  empty = false,
  emptyMessage = '게시물이 없습니다.',
  className = '',
  ...rest
}: FeedListProps) => {
  if (loading) {
    return (
      <div className='flex items-center justify-center py-12'>
        <RefreshCw size={32} className='animate-spin text-gray-400' />
      </div>
    );
  }

  if (empty) {
    return (
      <div className='flex flex-col items-center justify-center py-12'>
        <Text typography='body1' color='gray500'>
          {emptyMessage}
        </Text>
      </div>
    );
  }

  return (
    <div className={`divide-y divide-gray-200 ${className}`} {...rest}>
      {children}
    </div>
  );
};

export default FeedList;
