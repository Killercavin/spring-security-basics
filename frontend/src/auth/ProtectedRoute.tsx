import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth, type Role } from "./AuthContext";
import { AppLoader } from "../components/AppLoader";
import { ROUTE_PATHS } from "../router/constants";

interface ProtectedRouteProps {
  allowedRoles?: Role[];
}

export default function ProtectedRoute({ allowedRoles }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading, user } = useAuth();
  const location = useLocation();

  // Show loader while auth is being restored
  if (isLoading) {
    return <AppLoader />;
  }

  // Not authenticated — send to login, preserve intended destination
  if (!isAuthenticated) {
    return <Navigate to={ROUTE_PATHS.LOGIN} state={{ from: location }} replace />;
  }

  // Authenticated but wrong role
  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
    return <Navigate to={ROUTE_PATHS.UNAUTHORIZED} replace />;
  }

  return <Outlet />;
}