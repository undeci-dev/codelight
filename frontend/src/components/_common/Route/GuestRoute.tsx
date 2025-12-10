import { Navigate, Outlet } from 'react-router-dom';
import { ROUTE_PATH } from '@/constants/routhPath';
import useAuthStore from '@/store/useAuthStore';

const GuestRoute = () => {
  const isLoggedIn = useAuthStore((state) => !!state.accessToken);

  if (isLoggedIn) {
    return <Navigate to={ROUTE_PATH.feed} replace />;
  }

  return <Outlet />;
};

export default GuestRoute;
