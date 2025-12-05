import { HTMLAttributes } from 'react';
import { Loader2 } from 'lucide-react';

type SpinnerSize = 'sm' | 'md' | 'lg';

interface LoadingSpinnerProps extends HTMLAttributes<HTMLDivElement> {
  size?: SpinnerSize;
}

const LoadingSpinner = ({
  size = 'md',
  className = '',
  ...rest
}: LoadingSpinnerProps) => {
  const sizeClasses: Record<SpinnerSize, number> = {
    sm: 16,
    md: 24,
    lg: 32,
  };

  return (
    <div className={`flex items-center justify-center ${className}`} {...rest}>
      <Loader2
        size={sizeClasses[size]}
        className='animate-spin text-purple-600'
      />
    </div>
  );
};

export default LoadingSpinner;
