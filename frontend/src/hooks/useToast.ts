import { useEffect } from 'react';

import { DEFAULT_TOAST_DURATION } from '@/constants/system';
import useToastStore from '@/store/useToastStore';

interface Props {
  durationSecond?: number;
}

const useToast = (props?: Props) => {
  const { toast, type, addToast, removeToast } = useToastStore();

  const { durationSecond = DEFAULT_TOAST_DURATION } = props || {};

  useEffect(() => {
    if (toast) {
      const timer = setTimeout(() => {
        removeToast();
      }, durationSecond * 1000);
      return () => clearTimeout(timer);
    }
  }, [toast, durationSecond, removeToast]);

  return { toast, type, addToast };
};

export default useToast;
