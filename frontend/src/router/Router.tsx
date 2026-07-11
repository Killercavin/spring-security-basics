import { createBrowserRouter, Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import ProtectedRoute from "../auth/ProtectedRoute";
import Landing from "../pages/Landing";
import Login from "../pages/auth/Login";
import Unauthorized from "../pages/auth/Unauthorized";
import NotFound from "../pages/NotFound";
import { ROUTE_PATHS, getRoleDestination, routeConfigs } from "./constants";

// ── Role-based default redirect after login ────────────────────────────────
export function RoleRedirect() {
  const { user } = useAuth();
  const destination = getRoleDestination(user?.role);
  return <Navigate to={destination} replace />;
}

// ── Router ──────────────────────────────────────────────────────────────────
export const router = createBrowserRouter([
  // Public routes
  {
    path: ROUTE_PATHS.ROOT,
    element: <Landing />,
  },
  {
    path: ROUTE_PATHS.LOGIN,
    element: <Login />,
  },
  {
    path: ROUTE_PATHS.UNAUTHORIZED,
    element: <Unauthorized />,
  },

  // Authenticated — any role
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: ROUTE_PATHS.NEW_VISITOR,
        lazy: routeConfigs.lazyLoad("../pages/visitors/NewVisitor"),
      },
      {
        path: ROUTE_PATHS.VISITORS,
        lazy: routeConfigs.lazyLoad("../pages/visitors/VisitorList"),
      },
    ],
  },

  // Manager + Super Admin
  {
    element: <ProtectedRoute allowedRoles={["MANAGER", "SUPER_ADMIN"]} />,
    children: [
      {
        path: ROUTE_PATHS.DASHBOARD,
        lazy: routeConfigs.lazyLoad("../pages/Dashboard"),
      },
      {
        path: ROUTE_PATHS.REPORTS,
        lazy: routeConfigs.lazyLoad("../pages/Reports"),
      },
      {
        path: ROUTE_PATHS.USERS,
        lazy: routeConfigs.lazyLoad("../pages/users/Users"),
      },
    ],
  },

  // Super Admin only
  {
    element: <ProtectedRoute allowedRoles={["SUPER_ADMIN"]} />,
    children: [
      {
        path: ROUTE_PATHS.ADMIN,
        lazy: routeConfigs.lazyLoad("../pages/users/Admin"),
      },
      {
        path: ROUTE_PATHS.SITES,
        lazy: routeConfigs.lazyLoad("../pages/sites/Sites"),
      },
    ],
  },

  // Catch-all
  {
    path: "*",
    element: <NotFound />,
  },
]);