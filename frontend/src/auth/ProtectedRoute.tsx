import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth, type Role } from "./AuthContext";

interface ProtectedRouteProps {
  allowedRoles?: Role[];
}

export default function ProtectedRoute({ allowedRoles }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading, user } = useAuth();
  const location = useLocation();

  // still bootstrapping from localStorage — render nothing
  if (isLoading) return <AppLoader />;

  // not authenticated — send to login, preserve intended destination
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // authenticated but wrong role
  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <Outlet />;
}

function AppLoader() {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        height: "100vh",
        background: "var(--white)",
      }}
      aria-label="Loading"
      role="status"
    >
      <svg
        width="32"
        height="32"
        viewBox="0 0 32 32"
        fill="none"
        aria-hidden="true"
        style={{ animation: "spin 0.8s linear infinite" }}
      >
        <circle
          cx="16"
          cy="16"
          r="13"
          stroke="var(--neutral-200)"
          strokeWidth="3"
        />
        <path
          d="M16 3a13 13 0 0113 13"
          stroke="var(--green-500)"
          strokeWidth="3"
          strokeLinecap="round"
        />
      </svg>
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}