import { create } from 'zustand';

interface AuthState {
  accessToken: string | null;
  setAccessToken: (accessToken: string) => void;
  clearAccessToken: () => void;
  getAccessToken: () => string | null;
}

const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: null,
  setAccessToken: (accessToken: string) => {
    set({ accessToken });
  },
  clearAccessToken: () => {
    set({ accessToken: null });
  },
  getAccessToken: () => get().accessToken,
}));

export default useAuthStore;
