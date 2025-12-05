import { HTMLAttributes } from 'react';
import Text from '@/components/_common/Text/Text';

interface CharacterCounterProps extends HTMLAttributes<HTMLDivElement> {
  current: number;
  max: number;
}

const CharacterCounter = ({
  current,
  max,
  className = '',
  ...rest
}: CharacterCounterProps) => {
  const percentage = (current / max) * 100;
  const isWarning = percentage >= 80;
  const isError = percentage >= 100;

  return (
    <div className={`flex items-center gap-2 ${className}`} {...rest}>
      <Text
        typography='caption'
        color={isError ? 'error' : isWarning ? 'warning' : 'gray500'}
      >
        {current}/{max}
      </Text>
    </div>
  );
};

export default CharacterCounter;
