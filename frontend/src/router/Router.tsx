import { createBrowserRouter } from 'react-router-dom'
import Landing from '../pages/Landing'
import Login from '../pages/auth/Login'
import Unauthorized from '../pages/auth/Unauthorized'
import NotFound from '../pages/NotFound'
import ProtectedRoute from '../auth/ProtectedRoute'
import AppShell from '../components/layout/AppShell'
import { ROUTE_PATHS } from './constants'

export const router = createBrowserRouter([
  // Public
  { path: ROUTE_PATHS.ROOT, element: <Landing /> },
  { path: ROUTE_PATHS.LOGIN, element: <Login /> },
  { path: ROUTE_PATHS.UNAUTHORIZED, element: <Unauthorized /> },

  // Authenticated: shell wraps all protected routes
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppShell />,
        children: [
          // any authenticated role
          {
            path: ROUTE_PATHS.NEW_VISITOR,
            lazy: () => import('../pages/visitors/NewVisitor')
              .then((m) => ({ Component: m.default })),
          },
          {
            path: ROUTE_PATHS.VISITORS,
            lazy: () => import('../pages/visitors/VisitorList')
              .then((m) => ({ Component: m.default })),
          },

          // manager + super admin only
          {
            element: <ProtectedRoute allowedRoles={['MANAGER', 'SUPER_ADMIN']} />,
            children: [
              {
                path: ROUTE_PATHS.DASHBOARD,
                lazy: () => import('../pages/Dashboard')
                  .then((m) => ({ Component: m.default })),
              },
              {
                path: ROUTE_PATHS.REPORTS,
                lazy: () => import('../pages/Reports')
                  .then((m) => ({ Component: m.default })),
              },
              {
                path: ROUTE_PATHS.USERS,
                lazy: () => import('../pages/users/Users')
                  .then((m) => ({ Component: m.default })),
              },
            ],
          },

          // super admin only
          {
            element: <ProtectedRoute allowedRoles={['SUPER_ADMIN']} />,
            children: [
              {
                path: ROUTE_PATHS.ADMIN,
                lazy: () => import('../pages/users/Admin')
                  .then((m) => ({ Component: m.default })),
              },
              {
                path: ROUTE_PATHS.SITES,
                lazy: () => import('../pages/sites/Sites')
                  .then((m) => ({ Component: m.default })),
              },
            ],
          },
        ],
      },
    ],
  },

  // Catch-all
  { path: '*', element: <NotFound /> },
])