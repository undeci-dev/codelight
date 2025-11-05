import React from 'react';

type Typography =
  | 'h1'
  | 'h2'
  | 'h3'
  | 'h4'
  | 'body1'
  | 'body2'
  | 'caption'
  | 'button';
type TextColor =
  | 'black'
  | 'white'
  | 'gray900'
  | 'gray800'
  | 'gray700'
  | 'gray600'
  | 'gray500'
  | 'gray400'
  | 'gray300'
  | 'gray200'
  | 'gray100'
  | 'primary'
  | 'secondary'
  | 'success'
  | 'warning'
  | 'error';

interface Props extends Omit<React.HTMLAttributes<HTMLSpanElement>, 'color'> {
  typography?: Typography;
  color?: TextColor;
  className?: string;
  children: React.ReactNode;
}

function Text({
  typography = 'body1',
  color = 'black',
  children,
  className = '',
  ...rest
}: Props) {
  const typographyClasses: Record<Typography, string> = {
    h1: 'text-4xl font-bold leading-tight',
    h2: 'text-3xl font-bold leading-tight',
    h3: 'text-2xl font-semibold leading-snug',
    h4: 'text-xl font-semibold leading-snug',
    body1: 'text-base font-normal leading-normal',
    body2: 'text-sm font-normal leading-normal',
    caption: 'text-xs font-normal leading-tight',
    button: 'text-sm font-medium leading-none',
  };

  const colorClasses: Record<TextColor, string> = {
    black: 'text-black',
    white: 'text-white',
    gray900: 'text-gray-900',
    gray800: 'text-gray-800',
    gray700: 'text-gray-700',
    gray600: 'text-gray-600',
    gray500: 'text-gray-500',
    gray400: 'text-gray-400',
    gray300: 'text-gray-300',
    gray200: 'text-gray-200',
    gray100: 'text-gray-100',
    primary: 'text-blue-600',
    secondary: 'text-purple-600',
    success: 'text-green-600',
    warning: 'text-yellow-600',
    error: 'text-red-600',
  };

  const classes = [
    typographyClasses[typography],
    colorClasses[color],
    className,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <span className={classes} {...rest}>
      {children}
    </span>
  );
}

export default Text;
