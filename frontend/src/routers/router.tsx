import React, { Suspense } from 'react';
import { createBrowserRouter, Outlet } from 'react-router-dom';
import { ROUTE_PATH } from '@/constants/routhPath';

const SignUpPage = React.lazy(() => import('@/pages/SignUpPage'));

const router = createBrowserRouter([
  {
    element: (
      <Suspense>
        <Outlet />
      </Suspense>
    ),
    children: [
      {
        element: <SignUpPage />,
        path: ROUTE_PATH.signUp,
      },
    ],
  },
]);
export default router;
