import { createBrowserRouter, Navigate } from "react-router-dom";
import { useAuth, type Role } from "../auth/AuthContext";
import ProtectedRoute from "../auth/ProtectedRoute";
import Landing from "../pages/Landing";
import Login from "../pages/auth/Login";
import Unauthorized from "../pages/auth/Unauthorized";
import NotFound from "../pages/NotFound";

// ── Role-based default redirect after login ────────────────────────────────
export function RoleRedirect() {
  const { user } = useAuth();
  const destination = getRoleDestination(user?.role);
  return <Navigate to={destination} replace />;
}

export function getRoleDestination(role: Role | undefined): string {
  switch (role) {
    case "SUPER_ADMIN": return "/admin";
    case "MANAGER":     return "/dashboard";
    case "STAFF":       return "/visitors/new";
    default:            return "/login";
  }
}

export const router = createBrowserRouter([
  // ── Public ──────────────────────────────────────────────────────────────
  {
    path: "/",
    element: <Landing />,
  },
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/unauthorized",
    element: <Unauthorized />,
  },

  // ── Authenticated — any role ─────────────────────────────────────────────
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: "/visitors/new",
        lazy: () => import("../pages/visitors/NewVisitor").then((m) => ({
          Component: m.default,
        })),
      },
      {
        path: "/visitors",
        lazy: () => import("../pages/visitors/VisitorList").then((m) => ({
          Component: m.default,
        })),
      },
    ],
  },

  // ── Manager + Super Admin ────────────────────────────────────────────────
  {
    element: <ProtectedRoute allowedRoles={["MANAGER", "SUPER_ADMIN"]} />,
    children: [
      {
        path: "/dashboard",
        lazy: () => import("../pages/Dashboard").then((m) => ({
          Component: m.default,
        })),
      },
      {
        path: "/reports",
        lazy: () => import("../pages/Reports").then((m) => ({
          Component: m.default,
        })),
      },
      {
        path: "/users",
        lazy: () => import("../pages/users/Users").then((m) => ({
          Component: m.default,
        })),
      },
    ],
  },

  // ── Super Admin only ─────────────────────────────────────────────────────
  {
    element: <ProtectedRoute allowedRoles={["SUPER_ADMIN"]} />,
    children: [
      {
        path: "/admin",
        lazy: () => import("../pages/users/Admin").then((m) => ({
          Component: m.default,
        })),
      },
      {
        path: "/sites",
        lazy: () => import("../pages/sites/Sites").then((m) => ({
          Component: m.default,
        })),
      },
    ],
  },

  // ── Catch-all ────────────────────────────────────────────────────────────
  {
    path: "*",
    element: <NotFound />,
  },
]);