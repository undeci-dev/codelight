import type { LinkPreviewResponse } from '@/types/link';
import FeedLinkPreview from './FeedLinkPreview';

interface LinkPreviewListProps {
  links: LinkPreviewResponse[];
  onRemoveLink?: (url: string) => void;
}

const FeedLinkPreviewList = ({ links, onRemoveLink }: LinkPreviewListProps) => {
  if (!links || links.length === 0) return null;

  return (
    <div className='mt-3 space-y-2'>
      {links.map((link) => (
        <FeedLinkPreview
          key={link.url}
          link={link}
          onRemove={onRemoveLink ? () => onRemoveLink(link.url) : undefined}
        />
      ))}
    </div>
  );
};

export default FeedLinkPreviewList;
