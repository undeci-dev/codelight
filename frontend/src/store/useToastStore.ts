import { create } from 'zustand';
import { ToastType } from '@/types/toast';

interface toastState {
  toast: string | null;
  type: ToastType;
  addToast: ({ message, type }: { message: string; type?: ToastType }) => void;
  removeToast: () => void;
}

const useToastStore = create<toastState>((set) => ({
  toast: null,
  type: 'success',
  addToast: ({
    message,
    type = 'success',
  }: {
    message: string;
    type?: ToastType;
  }) => {
    set((state) => ({ ...state, toast: message, type: type }));
  },
  removeToast: () => {
    set((state) => ({ ...state, toast: null }));
  },
}));

export default useToastStore;
