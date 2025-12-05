import { useNavigate } from 'react-router-dom';
import Button from '@/components/_common/Button/Button';
import { ROUTE_PATH } from '@/constants/routhPath';
import logo from '@/assets/images/common/codelight-logo.svg';

interface NavbarProps {
  isLoggedIn?: boolean;
  onLogout?: () => void;
  showAuthButtons?: boolean;
}

const Navbar = ({
  isLoggedIn = false,
  onLogout,
  showAuthButtons = true,
}: NavbarProps) => {
  const navigate = useNavigate();

  const handleLogin = () => {
    navigate(ROUTE_PATH.signIn);
  };

  const handleSignUp = () => {
    navigate(ROUTE_PATH.signUp);
  };

  return (
    <nav className='sticky top-0 z-20 border-b border-gray-200 bg-white px-4 py-3'>
      <div className='mx-auto flex max-w-2xl items-center justify-between'>
        <img
          src={logo}
          alt='CodeLight'
          className='h-8 cursor-pointer'
          onClick={() => navigate(isLoggedIn ? ROUTE_PATH.main : '/')}
        />

        {showAuthButtons && (
          <div className='flex items-center gap-2'>
            {isLoggedIn ? (
              <Button
                label='로그아웃'
                variant='secondary'
                size='sm'
                onClick={onLogout}
              />
            ) : (
              <>
                <Button
                  label='로그인'
                  variant='secondary'
                  size='sm'
                  onClick={handleLogin}
                />
                <Button
                  label='회원가입'
                  variant='primary'
                  size='sm'
                  onClick={handleSignUp}
                />
              </>
            )}
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
