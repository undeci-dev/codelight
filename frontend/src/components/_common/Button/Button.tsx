import React, { ComponentProps, FunctionComponent, SVGProps } from 'react';
import type { LucideIcon } from 'lucide-react';

type ButtonVariant = 'primary' | 'secondary' | 'social' | 'link';
type ButtonSize = 'sm' | 'md' | 'lg';
type ButtonType = 'button' | 'submit' | 'reset';
type SocialProvider = 'google' | 'kakao' | 'naver';

interface Props extends ComponentProps<'button'> {
  type?: ButtonType;
  variant?: ButtonVariant;
  size?: ButtonSize;
  label: React.ReactNode;
  fullWidth?: boolean;
  disabled?: boolean;
  Icon?: FunctionComponent<SVGProps<SVGSVGElement>> | LucideIcon;
  iconPosition?: 'start' | 'end';
  socialProvider?: SocialProvider;
  onClick?: () => void;
  id?: string;
}

const Button = ({
  label,
  onClick,
  type = 'button',
  variant = 'primary',
  size = 'md',
  fullWidth = false,
  disabled = false,
  Icon,
  className = '',
  socialProvider,
  iconPosition = 'end',
  id,
  ...rest
}: Props) => {
  const baseClasses =
    'inline-flex items-center justify-center gap-2 font-medium transition-all focus:outline-none focus:ring-2 rounded-lg';

  const sizeClasses = {
    sm: 'px-3 py-2 text-sm',
    md: 'px-6 py-3 text-sm',
    lg: 'px-8 py-4 text-base',
  };

  const getSocialClasses = (provider?: SocialProvider): string => {
    switch (provider) {
      case 'google':
        return 'bg-white border border-gray-200 text-gray-700 hover:bg-gray-50 hover:border-gray-300';
      case 'kakao':
        return 'bg-yellow-400 border border-yellow-400 text-gray-900 hover:bg-yellow-500';
      case 'naver':
        return 'bg-green-500 border border-green-500 text-white hover:bg-green-600';
      default:
        return 'bg-white border border-gray-200 text-gray-700 hover:bg-gray-50 hover:border-gray-300';
    }
  };

  const variantClasses = {
    primary:
      'bg-purple-600 text-white hover:bg-purple-700 focus:ring-purple-200',
    secondary:
      'bg-white border border-gray-200 text-gray-700 hover:bg-gray-50 hover:border-gray-300',
    social: getSocialClasses(socialProvider),
    link: 'text-purple-600 hover:text-purple-700 focus:ring-purple-200',
  };

  const classes = [
    baseClasses,
    sizeClasses[size],
    variantClasses[variant],
    fullWidth ? 'w-full' : '',
    disabled ? 'opacity-50 cursor-not-allowed' : '',
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <button
      id={id}
      type={type}
      onClick={disabled ? undefined : onClick}
      disabled={disabled}
      className={classes}
      {...rest}
    >
      {iconPosition === 'start' && Icon && (
        <Icon className='h-4 w-4' aria-hidden='true' />
      )}
      {label}
      {iconPosition === 'end' && Icon && (
        <Icon className='h-4 w-4' aria-hidden='true' />
      )}
    </button>
  );
};

export default Button;
