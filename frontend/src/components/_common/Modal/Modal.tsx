import { ReactNode, useEffect } from 'react';
import { X } from 'lucide-react';
import Text from '@/components/_common/Text/Text';

type ModalSize = 'sm' | 'md' | 'lg' | 'xl';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  children: ReactNode;
  size?: ModalSize;
  showCloseButton?: boolean;
}

const Modal = ({
  isOpen,
  onClose,
  title,
  children,
  size = 'md',
  showCloseButton = true,
}: ModalProps) => {
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener('keydown', handleEscape);
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('keydown', handleEscape);
      document.body.style.overflow = 'unset';
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const sizeClasses: Record<ModalSize, string> = {
    sm: 'max-w-md',
    md: 'max-w-lg',
    lg: 'max-w-2xl',
    xl: 'max-w-4xl',
  };

  return (
    <div className='fixed inset-0 z-50 flex items-center justify-center'>
      {/* Backdrop */}
      <div
        className='absolute inset-0 bg-black bg-opacity-50'
        onClick={onClose}
      />

      {/* Modal */}
      <div
        className={`relative w-full ${sizeClasses[size]} max-h-[90vh] overflow-hidden rounded-lg bg-white shadow-xl`}
      >
        {/* Header */}
        {(title || showCloseButton) && (
          <div className='flex items-center justify-between border-b border-gray-200 px-6 py-4'>
            {title && (
              <Text typography='h3' color='gray900'>
                {title}
              </Text>
            )}
            {showCloseButton && (
              <button
                onClick={onClose}
                className='rounded-full p-2 hover:bg-gray-100'
              >
                <X size={20} className='text-gray-600' />
              </button>
            )}
          </div>
        )}

        {/* Content */}
        <div
          className='overflow-y-auto'
          style={{ maxHeight: 'calc(90vh - 80px)' }}
        >
          {children}
        </div>
      </div>
    </div>
  );
};

export default Modal;
