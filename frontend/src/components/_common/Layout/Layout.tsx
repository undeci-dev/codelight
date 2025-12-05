import { Outlet, useNavigate } from 'react-router-dom';
import Navbar from '@/components/_common/Navbar/Navbar';
import useAuthStore from '@/store/useAuthStore';
import { useLogOutMutation } from '@/hooks/query/useAuthQuery';
import { ROUTE_PATH } from '@/constants/routhPath';

const Layout = () => {
  const navigate = useNavigate();
  const { accessToken } = useAuthStore();
  const { mutate } = useLogOutMutation();

  const handleLogOut = () => {
    mutate(undefined, {
      onSuccess: () => {
        setTimeout(() => {
          navigate(ROUTE_PATH.signIn);
        }, 1000);
      },
    });
  };

  return (
    <div className='min-h-screen bg-gray-50'>
      <Navbar isLoggedIn={!!accessToken} onLogout={handleLogOut} />
      <Outlet />
    </div>
  );
};

export default Layout;
