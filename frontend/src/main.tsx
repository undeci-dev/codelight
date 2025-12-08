import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { HelmetProvider } from 'react-helmet-async';
import './index.css';
import App from './App.tsx';
import { initKakaoSDK } from './utils/share';

// 카카오 SDK 초기화
const kakaoAppKey = import.meta.env.VITE_KAKAO_CLIENT_ID;
if (kakaoAppKey) {
  initKakaoSDK(kakaoAppKey);
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <HelmetProvider>
      <App />
    </HelmetProvider>
  </StrictMode>
);
