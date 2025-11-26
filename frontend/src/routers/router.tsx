import React, { Suspense } from 'react';
import { createBrowserRouter, Outlet } from 'react-router-dom';
import { ROUTE_PATH } from '@/constants/routhPath';

const MainPage = React.lazy(() => import('@/pages/MainPage'));
const SignUpPage = React.lazy(() => import('@/pages/SignUpPage'));
const SignInPage = React.lazy(() => import('@/pages/SignInPage'));
const KakaoCallbackPage = React.lazy(() => import('@/pages/KakaoCallbackPage'));

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
      {
        element: <SignInPage />,
        path: ROUTE_PATH.signIn,
      },
      {
        element: <KakaoCallbackPage />,
        path: ROUTE_PATH.kakaoCallback,
      },
      {
        element: <MainPage />,
        path: ROUTE_PATH.main,
      },
    ],
  },
]);
export default router;
