import React, { Suspense } from 'react';
import { createBrowserRouter } from 'react-router-dom';
import { ROUTE_PATH } from '@/constants/routhPath';
import Layout from '@/components/_common/Layout/Layout';
import GuestRoute from '@/components/_common/Route/GuestRoute';

const MainPage = React.lazy(() => import('@/pages/MainPage'));
const FeedPage = React.lazy(() => import('@/pages/FeedPage'));
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
      // 비로그인 사용자만 접근 가능
      {
        element: <GuestRoute />,
        children: [
          {
            element: <SignUpPage />,
            path: ROUTE_PATH.signUp,
          },
          {
            element: <SignInPage />,
            path: ROUTE_PATH.signIn,
          },
        ],
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
        element: <FeedPage />,
        path: ROUTE_PATH.feed,
      },
      {
        element: <PostDetailPage />,
        path: ROUTE_PATH.postDetail,
      },
    ],
  },
]);
export default router;
