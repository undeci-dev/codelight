import { HTMLAttributes } from 'react';

type AvatarSize = 'sm' | 'md' | 'lg' | 'xl';

interface AvatarProps extends HTMLAttributes<HTMLDivElement> {
  name: string;
  alt?: string;
  src?: string;
  size?: AvatarSize;
}

const Avatar = ({
  name,
  alt,
  src,
  size = 'md',
  className = '',
  ...rest
}: AvatarProps) => {
  const sizeClasses: Record<AvatarSize, string> = {
    sm: 'h-8 w-8 text-sm',
    md: 'h-10 w-10 text-base',
    lg: 'h-12 w-12 text-lg',
    xl: 'h-16 w-16 text-xl',
  };

  const getInitial = () => {
    return name.charAt(0).toUpperCase();
  };

  return (
    <div
      className={`flex items-center justify-center rounded-full bg-gray-300 ${sizeClasses[size]} ${className}`}
      {...rest}
    >
      {src ? (
        <img
          src={src}
          alt={alt || name}
          className='h-full w-full rounded-full object-cover'
        />
      ) : (
        <span className='font-semibold text-white'>{getInitial()}</span>
      )}
    </div>
  );
};

export default Avatar;
