import { X } from 'lucide-react';
import type { LinkPreviewResponse } from '@/types/link';
import Text from '@/components/_common/Text/Text';

interface LinkPreviewProps {
  link: LinkPreviewResponse;
  onRemove?: () => void;
}

const FeedLinkPreview = ({ link, onRemove }: LinkPreviewProps) => {
  return (
    <div className='relative overflow-hidden rounded-lg border border-gray-200 bg-white hover:bg-gray-50'>
      <a
        href={link.url}
        target='_blank'
        rel='noopener noreferrer'
        className='flex h-20'
        onClick={(e) => e.stopPropagation()}
      >
        {link.image && (
          <div className='h-20 w-20 flex-shrink-0 overflow-hidden bg-gray-100'>
            <img
              src={link.image}
              alt={link.title || link.url}
              className='h-full w-full object-cover'
            />
          </div>
        )}

        <div className='flex min-w-0 flex-1 flex-col justify-center px-3 py-2'>
          {link.domain && (
            <Text typography='caption' color='gray500' className='truncate'>
              {link.domain}
            </Text>
          )}

          {link.title && (
            <Text
              typography='body2'
              color='gray900'
              className='line-clamp-1 font-semibold'
            >
              {link.title}
            </Text>
          )}

          {link.description && (
            <Text typography='caption' color='gray600' className='line-clamp-1'>
              {link.description}
            </Text>
          )}
        </div>
      </a>

      {onRemove && (
        <button
          onClick={onRemove}
          className='absolute right-1 top-1 rounded-full bg-gray-900 bg-opacity-70 p-0.5 text-white hover:bg-opacity-90'
        >
          <X size={14} />
        </button>
      )}
    </div>
  );
};

export default FeedLinkPreview;
