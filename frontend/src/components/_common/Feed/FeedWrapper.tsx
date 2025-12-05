import { HTMLAttributes, ReactNode } from 'react';

interface FeedWrapperProps extends HTMLAttributes<HTMLDivElement> {
  children: ReactNode;
}

const FeedWrapper = ({
  children,
  className = '',
  ...rest
}: FeedWrapperProps) => {
  return (
    <div className={`min-h-screen bg-gray-50 ${className}`} {...rest}>
      <div className='mx-auto max-w-2xl px-4 py-6'>{children}</div>
    </div>
  );
};

export default FeedWrapper;
