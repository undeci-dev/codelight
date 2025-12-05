import React, { Suspense } from 'react';
import { createBrowserRouter, Outlet } from 'react-router-dom';
import { ROUTE_PATH } from '@/constants/routhPath';
import Layout from '@/components/_common/Layout/Layout';

const MainPage = React.lazy(() => import('@/pages/MainPage'));
const SignUpPage = React.lazy(() => import('@/pages/SignUpPage'));
const SignInPage = React.lazy(() => import('@/pages/SignInPage'));
const KakaoCallbackPage = React.lazy(() => import('@/pages/KakaoCallbackPage'));
const PostDetailPage = React.lazy(() => import('@/pages/PostDetailPage'));

const router = createBrowserRouter([
  {
    element: (
      <Suspense>
        <Layout />
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
      {
        element: <PostDetailPage />,
        path: ROUTE_PATH.postDetail,
      },
    ],
  },
]);
export default router;
