import { useState, useEffect } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../../auth/AuthContext'
import { ROUTE_PATHS } from '../../router/constants'
import {
  LayoutDashboard,
  UserPlus,
  Users,
  FileText,
  Building2,
  LogOut,
  Menu,
  X,
  ChevronRight,
  Shield,
} from 'lucide-react'

interface NavItem {
  label: string
  path: string
  icon: React.ReactNode
  roles: Array<'SUPER_ADMIN' | 'MANAGER' | 'STAFF'>
}

const NAV_ITEMS: NavItem[] = [
  {
    label: 'Dashboard',
    path: ROUTE_PATHS.DASHBOARD,
    icon: <LayoutDashboard size={17} />,
    roles: ['SUPER_ADMIN', 'MANAGER'],
  },
  {
    label: 'New Visitor',
    path: ROUTE_PATHS.NEW_VISITOR,
    icon: <UserPlus size={17} />,
    roles: ['SUPER_ADMIN', 'MANAGER', 'STAFF'],
  },
  {
    label: 'Visitors',
    path: ROUTE_PATHS.VISITORS,
    icon: <Users size={17} />,
    roles: ['SUPER_ADMIN', 'MANAGER', 'STAFF'],
  },
  {
    label: 'Reports',
    path: ROUTE_PATHS.REPORTS,
    icon: <FileText size={17} />,
    roles: ['SUPER_ADMIN', 'MANAGER'],
  },
  {
    label: 'Users',
    path: ROUTE_PATHS.USERS,
    icon: <Users size={17} />,
    roles: ['SUPER_ADMIN', 'MANAGER'],
  },
  {
    label: 'Sites',
    path: ROUTE_PATHS.SITES,
    icon: <Building2 size={17} />,
    roles: ['SUPER_ADMIN'],
  },
]

const ROLE_LABELS: Record<string, string> = {
  SUPER_ADMIN: 'Super Admin',
  MANAGER: 'Manager',
  STAFF: 'Staff',
}

function getInitials(email: string | undefined | null): string {
  if (!email) return "?"
  const parts = email.split("@")[0].split(/[._-]/)
  return parts
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase() ?? "")
    .join("")
}

export default function AppShell() {
  const { user, logout, isRole } = useAuth()
  const navigate = useNavigate()
  const [drawerOpen, setDrawerOpen] = useState(false)

  // close drawer on escape
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') setDrawerOpen(false)
    }
    document.addEventListener('keydown', onKey)
    return () => document.removeEventListener('keydown', onKey)
  }, [])

  async function handleLogout() {
    await logout()
    navigate(ROUTE_PATHS.LOGIN, { replace: true })
  }

  const visibleLinks = NAV_ITEMS.filter((item) =>
    item.roles.some((r) => isRole(r))
  )

  const initials = getInitials(user?.email)
  const roleLabel = user ? (ROLE_LABELS[user.role] ?? user.role) : ""

  return (
    <div className="flex min-h-screen bg-gray-50">
      {/* Sidebar overlay (mobile) */}
      {drawerOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/40 backdrop-blur-sm md:hidden"
          aria-hidden="true"
          onClick={() => setDrawerOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={[
          'fixed top-0 left-0 z-50 h-screen w-[240px] flex flex-col',
          'bg-white border-r border-gray-100',
          'transition-transform duration-300 ease-out',
          'md:translate-x-0',
          drawerOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0',
        ].join(' ')}
        aria-label="Sidebar navigation"
      >
        {/* Logo */}
        <div className="flex items-center justify-between h-16 px-5 border-b border-gray-100 flex-shrink-0">
          <NavLink
            to={ROUTE_PATHS.ROOT}
            className="font-display font-bold text-lg text-neutral-900 tracking-tight"
          >
            Gate<span className="text-green-500">log</span>
          </NavLink>
          <button
            className="md:hidden text-gray-400 hover:text-gray-600 p-1"
            onClick={() => setDrawerOpen(false)}
            aria-label="Close menu"
          >
            <X size={18} />
          </button>
        </div>

        {/* Nav links */}
        <nav className="flex-1 overflow-y-auto px-3 py-4" aria-label="Main">
          <ul role="list" className="flex flex-col gap-0.5">
            {visibleLinks.map((item) => (
              <li key={item.path}>
                <NavLink
                  to={item.path}
                  onClick={() => setDrawerOpen(false)}
                  className={({ isActive }) =>
                    [
                      'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150',
                      isActive
                        ? 'bg-green-50 text-green-700 font-semibold'
                        : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900',
                    ].join(' ')
                  }
                >
                  {({ isActive }) => (
                    <>
                      <span className={isActive ? 'text-green-600' : 'text-gray-400'}>
                        {item.icon}
                      </span>
                      <span className="flex-1">{item.label}</span>
                      {isActive && (
                        <ChevronRight size={14} className="text-green-500 opacity-60" />
                      )}
                    </>
                  )}
                </NavLink>
              </li>
            ))}
          </ul>

          {/* Super Admin section label */}
          {isRole('SUPER_ADMIN') && (
            <div className="mt-6 mb-2 px-3">
              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wider flex items-center gap-1.5">
                <Shield size={11} />
                Admin
              </p>
            </div>
          )}
        </nav>

        {/* User footer */}
        <div className="flex-shrink-0 border-t border-gray-100 p-3">
          <div className="flex items-center gap-3 px-2 py-2 rounded-lg">
            {/* Avatar */}
            <div className="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center flex-shrink-0">
              <span className="text-xs font-bold text-green-700">
                {initials}
              </span>
            </div>

            {/* User info */}
            <div className="flex-1 min-w-0">
              <p className="text-xs font-semibold text-gray-900 truncate">
                {user?.email}
              </p>
              <p className="text-xs text-gray-400">{roleLabel}</p>
            </div>
          </div>

          {/* Logout */}
          <button
            onClick={handleLogout}
            className="mt-1 w-full flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm font-medium text-gray-500 hover:bg-red-50 hover:text-red-600 transition-colors duration-150"
          >
            <LogOut size={15} />
            Sign out
          </button>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex-1 flex flex-col md:ml-[240px] min-h-screen">
        {/* Mobile topbar */}
        <header className="md:hidden flex items-center justify-between h-14 px-4 bg-white border-b border-gray-100 sticky top-0 z-30">
          <button
            className="text-gray-500 hover:text-gray-700 p-1"
            onClick={() => setDrawerOpen(true)}
            aria-label="Open menu"
            aria-expanded={drawerOpen}
          >
            <Menu size={20} />
          </button>

          <span className="font-display font-bold text-base text-neutral-900 tracking-tight">
            Gate<span className="text-green-500">log</span>
          </span>

          {/* Avatar in topbar */}
          <div className="w-7 h-7 rounded-full bg-green-100 flex items-center justify-center">
            <span className="text-xs font-bold text-green-700">{initials}</span>
          </div>
        </header>

        {/* Page content - Outlet renders the active route */}
        <main className="flex-1 p-6 md:p-8">
          <Outlet />
        </main>
      </div>
    </div>
  )
}