import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Code2, MessageSquare, Users, Zap } from 'lucide-react';
import { ROUTE_PATH } from '@/constants/routhPath';
import { useSignInMutation } from '@/hooks/query/useAuthQuery';
import useAuthStore from '@/store/useAuthStore';

const MainPage = () => {
  const navigate = useNavigate();
  const { mutate: signIn, isPending } = useSignInMutation();
  const isLoggedIn = useAuthStore((state) => !!state.accessToken);
  const [testLoginError, setTestLoginError] = useState('');

  const handleTestLogin = () => {
    setTestLoginError('');
    signIn(
      { email: 'test@test.com', password: 'test1234' },
      {
        onSuccess: () => {
          navigate(ROUTE_PATH.feed);
        },
        onError: (error) => {
          setTestLoginError(error.message || '테스트 로그인에 실패했습니다.');
        },
      }
    );
  };

  const handleGoToFeed = () => {
    navigate(ROUTE_PATH.feed);
  };

  const handleGoToSignIn = () => {
    navigate(ROUTE_PATH.signIn);
  };

  return (
    <div className='flex min-h-[calc(100dvh-56px)] flex-col'>
      {/* Hero Section */}
      <section className='flex flex-1 flex-col items-center justify-center bg-gradient-to-br from-purple-50 to-white px-4 py-16'>
        <div className='text-center'>
          <div className='mb-6 flex items-center justify-center gap-2'>
            <Code2 className='h-12 w-12 text-purple-600' />
            <h1 className='text-4xl font-bold text-gray-900'>CodeLight</h1>
          </div>
          <p className='mb-2 text-xl text-gray-600'>
            개발자를 위한 소셜 플랫폼
          </p>
          <p className='mb-8 text-gray-500'>
            코드를 공유하고, 아이디어를 나누고, 함께 성장하세요
          </p>

          <div className='flex flex-col items-center gap-4'>
            {isLoggedIn ? (
              <button
                onClick={handleGoToFeed}
                className='rounded-lg bg-purple-600 px-8 py-3 font-semibold text-white transition-colors hover:bg-purple-700'
              >
                피드로 이동
              </button>
            ) : (
              <>
                <button
                  onClick={handleTestLogin}
                  disabled={isPending}
                  className='rounded-lg bg-purple-600 px-8 py-3 font-semibold text-white transition-colors hover:bg-purple-700 disabled:cursor-not-allowed disabled:opacity-50'
                >
                  {isPending ? '로그인 중...' : '테스트 계정으로 시작하기'}
                </button>
                {testLoginError && (
                  <p className='text-sm text-red-500'>{testLoginError}</p>
                )}
                <button
                  onClick={handleGoToSignIn}
                  className='text-sm text-gray-500 underline hover:text-gray-700'
                >
                  다른 계정으로 로그인
                </button>
              </>
            )}
          </div>
        </div>
      </section>

      {/* Features Section */}
      {/* <section className='bg-white px-4 py-16'>
        <div className='mx-auto max-w-4xl'>
          <h2 className='mb-12 text-center text-2xl font-bold text-gray-900'>
            주요 기능
          </h2>
          <div className='grid gap-8 md:grid-cols-3'>
            <FeatureCard
              icon={<Users className='h-8 w-8 text-purple-600' />}
              title='개발자 커뮤니티'
              description='다른 개발자들과 지식을 공유하고 피드백을 주고받으세요'
            />
            <FeatureCard
              icon={<Zap className='h-8 w-8 text-purple-600' />}
              title='실시간 코드 공유'
              description='Code on Air로 실시간 코드 세션을 공유하세요'
            />
          </div>
        </div>
      </section> */}

      {/* Footer */}
      <footer className='bg-gray-50 px-4 py-8 text-center text-sm text-gray-500'>
        <p>CodeLight - Developer Social Platform</p>
        <p className='mt-1'>Built with React, TypeScript, TailwindCSS</p>
      </footer>
    </div>
  );
};

interface FeatureCardProps {
  icon: React.ReactNode;
  title: string;
  description: string;
}

const FeatureCard = ({ icon, title, description }: FeatureCardProps) => {
  return (
    <div className='rounded-xl border border-gray-100 bg-gray-50 p-6 text-center'>
      <div className='mb-4 flex justify-center'>{icon}</div>
      <h3 className='mb-2 font-semibold text-gray-900'>{title}</h3>
      <p className='text-sm text-gray-600'>{description}</p>
    </div>
  );
};

export default MainPage;
