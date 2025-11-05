import { useCallback } from 'react';
import { X } from 'lucide-react';

import { InputChangeEvent } from '@/types/event';

const widthSize = {
  small: 'w-[10rem]',
  medium: 'w-[16rem]',
  large: 'w-[30rem]',
  full: 'w-full',
};

interface Props
  extends React.InputHTMLAttributes<HTMLInputElement | HTMLTextAreaElement> {
  width?: keyof typeof widthSize;
  variant?: 'default' | 'no-border' | 'fill-white';
  isError?: boolean;
}

const Input = ({
  width = 'full',
  value,
  onChange,
  variant = 'default',
  disabled,
  isError = false,
  className = '',
  ...rest
}: Props) => {
  const handleChange = useCallback(
    (event: InputChangeEvent) => {
      if (!onChange) return;
      onChange(event);
    },
    [onChange]
  );

  const handleClear = () => {
    if (!onChange) return;

    const fakeEvent = {
      target: {
        value: '',
      },
    } as unknown as InputChangeEvent;

    onChange(fakeEvent);
  };

  const getVariantClasses = () => {
    if (disabled) {
      return 'text-gray-400 border-gray-300';
    }

    if (isError) {
      return 'border-red-300';
    }

    switch (variant) {
      case 'no-border':
        return 'border-transparent';
      case 'fill-white':
        return 'bg-white border-transparent';
      case 'default':
      default:
        return 'border-gray-300 focus:border-gray-600';
    }
  };

  return (
    <div className={`relative ${widthSize[width]}`}>
      <input
        value={value}
        onChange={handleChange}
        disabled={disabled}
        className={`box-border flex h-[5rem] w-full items-center justify-center rounded-[0.8rem] border bg-transparent px-[1.1rem] py-[0.6rem] text-[1.6rem] font-normal text-black focus:outline-none disabled:cursor-not-allowed ${getVariantClasses()} ${className} `}
        {...rest}
      />
      {isError && (
        <button
          type='button'
          onClick={handleClear}
          className='absolute right-[1rem] top-1/2 -translate-y-1/2 cursor-pointer'
          aria-label='입력 내용 지우기'
        >
          <X size={20} className='text-red-300' />
        </button>
      )}
    </div>
  );
};

export default Input;
