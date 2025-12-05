import { HTMLAttributes } from 'react';
import { RefreshCw } from 'lucide-react';
import Text from '@/components/_common/Text/Text';

interface FeedHeaderProps extends HTMLAttributes<HTMLDivElement> {
  title?: string;
  onRefresh?: () => void;
  refreshing?: boolean;
}

const FeedHeader = ({
  title = '피드',
  onRefresh,
  refreshing = false,
  className = '',
  ...rest
}: FeedHeaderProps) => {
  return (
    <div
      className={`sticky top-0 z-10 flex items-center justify-between border-b border-gray-200 bg-white px-4 py-3 ${className}`}
      {...rest}
    >
      <Text typography='h3' color='gray900'>
        {title}
      </Text>
      {onRefresh && (
        <button
          onClick={onRefresh}
          disabled={refreshing}
          className='rounded-full p-2 transition-colors hover:bg-gray-100'
          aria-label='새로고침'
        >
          <RefreshCw
            size={20}
            className={`text-gray-600 ${refreshing ? 'animate-spin' : ''}`}
          />
        </button>
      )}
    </div>
  );
};

export default FeedHeader;
