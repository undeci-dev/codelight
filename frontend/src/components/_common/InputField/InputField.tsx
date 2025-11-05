import { ElementType, HTMLAttributes, LabelHTMLAttributes } from 'react';
import { Asterisk } from 'lucide-react';

import Input from '@/components/_common/Input/Input';

type GetProps<T> = T extends React.FC<infer P> ? P : never;

interface LabelProps extends LabelHTMLAttributes<HTMLLabelElement> {
  label: string;
  bold?: boolean;
  required?: boolean;
  as?: ElementType;
}

interface InputFieldWrapperProps extends HTMLAttributes<HTMLDivElement> {
  rowGap?: string;
  children: React.ReactNode;
}

const InputFieldWrapper = ({
  rowGap = '0.5rem',
  children,
  className = '',
  ...rest
}: InputFieldWrapperProps) => {
  return (
    <div
      className={`flex flex-auto flex-col gap-2 ${className}`}
      style={rowGap !== '0.5rem' ? { rowGap } : undefined}
      {...rest}
    >
      {children}
    </div>
  );
};

const InputField = Object.assign(InputFieldWrapper, {
  Label: ({
    label,
    bold = false,
    required = false,
    className = '',
    ...rest
  }: LabelProps) => {
    return (
      <label
        className={`relative z-0 ${bold ? 'text-base font-bold' : 'text-sm font-semibold'} text-gray-700 ${className}`}
        {...rest}
      >
        {label}
        {required && (
          <Asterisk
            size={7}
            className='relative -top-[0.4rem] left-[0.2rem] inline text-red-500'
          />
        )}
      </label>
    );
  },

  TextBox: ({
    text,
    className = '',
    ...rest
  }: { text: string } & HTMLAttributes<HTMLDivElement>) => (
    <div
      className={`box-border h-[2rem] w-full py-[0.6rem] ${className}`}
      {...rest}
    >
      {text}
    </div>
  ),

  Input: ({ ...rest }: GetProps<typeof Input>) => <Input {...rest} />,

  ErrorMessage: ({
    value,
    className = '',
    ...rest
  }: { value?: string } & HTMLAttributes<HTMLParagraphElement>) => (
    <div className='h-6'>
      <p
        className={`text-sm font-normal text-red-500 transition-opacity duration-200 ${value ? 'opacity-100' : 'opacity-0'} ${className}`}
        aria-live='polite'
        {...rest}
      >
        {value || '\u00A0'}
      </p>
    </div>
  ),

  BottomEmptyBox: () => <div className='h-[1rem]' />,
});

export default InputField;
