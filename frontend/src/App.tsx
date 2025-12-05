import { QueryClientProvider } from '@tanstack/react-query';
import { RouterProvider } from 'react-router-dom';
import router from '@/routers/router';
import { queryClient } from '@/utils/queryClient';
import Toast from '@/components/_common/Toast/Toast';
import useToast from '@/hooks/useToast';
import useInitAuth from '@/hooks/useInitAuth';

function App() {
  const { toast, type } = useToast();
  const { isInitialized } = useInitAuth();

  if (!isInitialized) {
    return null;
  }

  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
      {toast && <Toast type={type} message={toast} />}
    </QueryClientProvider>
  );
}

export default App;
