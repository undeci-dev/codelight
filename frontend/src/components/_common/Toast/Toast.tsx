import { CheckCircle2, XCircle, Info, AlertTriangle } from 'lucide-react';
import { ToastType } from '@/types/toast';
import { cn } from '@/utils/cn';

interface Props {
  type: ToastType;
  message: string;
}

const getToastStyles = (type: ToastType) => {
  const styles = {
    success: {
      container: 'bg-green-50 border border-green-200',
      icon: 'text-green-600',
      text: 'text-green-800',
    },
    error: {
      container: 'bg-red-50 border border-red-200',
      icon: 'text-red-600',
      text: 'text-red-800',
    },
    warning: {
      container: 'bg-yellow-50 border border-yellow-200',
      icon: 'text-yellow-600',
      text: 'text-yellow-800',
    },
    info: {
      container: 'bg-blue-50 border border-blue-200',
      icon: 'text-blue-600',
      text: 'text-blue-800',
    },
  };

  return styles[type];
};

const renderIcon = (type: ToastType) => {
  const styles = getToastStyles(type);
  const iconProps = {
    size: 20,
    className: cn('flex-shrink-0', styles.icon),
  };

  if (type === 'success') return <CheckCircle2 {...iconProps} />;
  if (type === 'error') return <XCircle {...iconProps} />;
  if (type === 'warning') return <AlertTriangle {...iconProps} />;
  if (type === 'info') return <Info {...iconProps} />;
  return null;
};

const Toast = ({ type, message }: Props) => {
  const formattedMessage = message
    .split('\n')
    .map((line, index) => <span key={index}>{line}</span>);

  const styles = getToastStyles(type);

  return (
    <div
      className={cn(
        'fixed bottom-24 left-1/2 z-50',
        '-translate-x-1/2',
        'animate-slide-up'
      )}
    >
      <div
        className={cn(
          'flex items-center',
          'w-[36rem] gap-3',
          'rounded-xl px-6 py-4',
          'shadow-lg',
          'box-border',
          styles.container
        )}
      >
        {renderIcon(type)}
        <div
          className={cn(
            'flex w-full flex-col whitespace-pre-line text-[1.4rem] font-medium',
            styles.text
          )}
        >
          {formattedMessage}
        </div>
      </div>
    </div>
  );
};

export default Toast;
