import { useEffect } from 'react'
import { Link } from 'react-router-dom'
import { useDashboard } from '../../features/dashboard/useDashboard'
import { useSites } from '../../features/admin/useSites'
import { useAuth } from '../../auth/AuthContext'
import { ROUTE_PATHS } from '../../router/constants'
import {
  Building2,
  Users,
  UserCheck,
  Activity,
  ArrowRight,
  AlertTriangle,
  RefreshCw,
  Clock,
} from 'lucide-react'

export default function AdminPage() {
  useAuth()
  const {
    data,
    isLoading: dashLoading,
    isRefreshing,
    lastUpdated,
    refresh,
    checkOut,
    checkingOutId,
  } = useDashboard()
  const { sites, isLoading: sitesLoading } = useSites()

  useEffect(() => {
    document.title = 'Admin Overview — Gatelog'
  }, [])

  const isLoading = dashLoading || sitesLoading

  return (
    <div className="max-w-7xl mx-auto">
      {/* Page header */}
      <div className="flex items-start justify-between mb-8">
        <div>
          <p className="text-xs font-semibold text-green-600 uppercase tracking-wider mb-1">
            Super Admin
          </p>
          <h1 className="font-display text-2xl font-extrabold text-neutral-900 tracking-tight">
            System Overview
          </h1>
          {lastUpdated && (
            <p className="text-xs text-gray-400 mt-1 flex items-center gap-1">
              <Clock size={11} />
              Updated {formatTime(lastUpdated)}
              <span className="w-1.5 h-1.5 rounded-full bg-green-400 ml-1 animate-[pulse_2s_ease-in-out_infinite]" />
            </p>
          )}
        </div>

        <button
          onClick={refresh}
          disabled={isRefreshing}
          className="flex items-center gap-2 px-3.5 py-2 rounded-lg border border-gray-200 text-sm font-medium text-gray-600 hover:border-gray-300 hover:text-gray-900 transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          <RefreshCw size={14} className={isRefreshing ? 'animate-spin' : ''} />
          Refresh
        </button>
      </div>

      {isLoading ? (
        <AdminSkeleton />
      ) : (
        <>
          {/* Platform summary cards */}
          <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
            <StatCard
              label="Active Sites"
              value={sites.length}
              icon={<Building2 size={18} />}
              color="green"
              linkTo={ROUTE_PATHS.SITES}
            />
            <StatCard
              label="On Premises Now"
              value={data?.summary.currentlyOnPremises ?? 0}
              icon={<UserCheck size={18} />}
              color="blue"
            />
            <StatCard
              label="Checked In Today"
              value={data?.summary.checkedInToday ?? 0}
              icon={<Activity size={18} />}
              color="gray"
            />
            <StatCard
              label="Overdue"
              value={data?.summary.overdueCount ?? 0}
              icon={<AlertTriangle size={18} />}
              color={data?.summary.overdueCount ?? 0 > 0 ? 'amber' : 'gray'}
              highlight={(data?.summary.overdueCount ?? 0) > 0}
            />
          </div>

          {/* Quick actions */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
            <QuickActionCard
              title="Site Management"
              desc="Create and manage sites across the platform."
              icon={<Building2 size={20} className="text-green-600" />}
              linkTo={ROUTE_PATHS.SITES}
              linkLabel="Manage Sites"
              count={sites.length}
              countLabel="sites"
            />
            <QuickActionCard
              title="User Management"
              desc="Create, update, and deactivate user accounts."
              icon={<Users size={20} className="text-green-600" />}
              linkTo={ROUTE_PATHS.USERS}
              linkLabel="Manage Users"
            />
          </div>

          {/* Cross-site active visitors */}
          <div className="bg-white rounded-xl border border-gray-100 overflow-hidden mb-6">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-50">
              <h2 className="font-display font-bold text-sm text-neutral-900 tracking-tight">
                Active Visitors — All Sites
              </h2>
              <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-gray-100 text-gray-500">
                {data?.activeVisitors.length ?? 0}
              </span>
            </div>

            {!data?.activeVisitors.length ? (
              <EmptyState message="No active visitors across any site" />
            ) : (
              <ul role="list" className="divide-y divide-gray-50">
                {data.activeVisitors.map((v) => (
                  <li
                    key={v.id}
                    className="flex items-center gap-3 px-5 py-3.5 hover:bg-gray-50 transition-colors duration-100"
                  >
                    <div className="w-8 h-8 rounded-full bg-green-100 flex-shrink-0 flex items-center justify-center text-xs font-bold text-green-700">
                      {v.name.split(' ').slice(0, 2).map((n: string) => n[0]?.toUpperCase()).join('')}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-semibold text-neutral-900 truncate">
                        {v.name}
                      </p>
                      <p className="text-xs text-gray-400 truncate">
                        {v.zoneName ? `${v.zoneName} · ` : ''}
                        {v.visitorType} · {formatTime(new Date(v.checkInTime))}
                      </p>
                    </div>
                    <button
                      onClick={() => checkOut(v.id)}
                      disabled={checkingOutId === v.id}
                      className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold border border-gray-200 text-gray-600 hover:border-red-200 hover:bg-red-50 hover:text-red-600 transition-all duration-150 disabled:opacity-50 flex-shrink-0"
                    >
                      Check out
                    </button>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {/* Overdue across all sites */}
          {(data?.overdueVisitors.length ?? 0) > 0 && (
            <div className="bg-white rounded-xl border border-amber-200 overflow-hidden">
              <div className="flex items-center justify-between px-5 py-4 border-b border-amber-100 bg-amber-50">
                <h2 className="font-display font-bold text-sm text-amber-800 tracking-tight flex items-center gap-2">
                  <AlertTriangle size={14} />
                  Overdue Visitors - All Sites
                </h2>
                <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700">
                  {data!.overdueVisitors.length}
                </span>
              </div>
              <ul role="list" className="divide-y divide-gray-50">
                {data!.overdueVisitors.map((v) => (
                  <li
                    key={v.id}
                    className="flex items-center gap-3 px-5 py-3.5 hover:bg-gray-50 transition-colors duration-100"
                  >
                    <div className="w-8 h-8 rounded-full bg-amber-100 flex-shrink-0 flex items-center justify-center text-xs font-bold text-amber-700">
                      {v.name.split(' ').slice(0, 2).map((n: string) => n[0]?.toUpperCase()).join('')}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-semibold text-neutral-900 truncate">{v.name}</p>
                      <p className="text-xs text-gray-400 truncate">
                        {v.zoneName ? `${v.zoneName} · ` : ''}
                        {v.visitorType} · in since {formatTime(new Date(v.checkInTime))}
                      </p>
                    </div>
                    <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 flex-shrink-0">
                      Overdue
                    </span>
                    <button
                      onClick={() => checkOut(v.id)}
                      disabled={checkingOutId === v.id}
                      className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold border border-gray-200 text-gray-600 hover:border-red-200 hover:bg-red-50 hover:text-red-600 transition-all duration-150 disabled:opacity-50 flex-shrink-0"
                    >
                      Check out
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </>
      )}
    </div>
  )
}

/* Stat card */
const COLOR_MAP = {
  green: { bg: 'bg-white',     icon: 'bg-green-100 text-green-600', value: 'text-green-700' },
  blue:  { bg: 'bg-white',     icon: 'bg-blue-100 text-blue-600',   value: 'text-blue-700'  },
  gray:  { bg: 'bg-white',     icon: 'bg-gray-100 text-gray-500',   value: 'text-gray-700'  },
  amber: { bg: 'bg-amber-50',  icon: 'bg-amber-100 text-amber-600', value: 'text-amber-700' },
}

function StatCard({
  label, value, icon, color, highlight, linkTo,
}: {
  label: string
  value: number
  icon: React.ReactNode
  color: keyof typeof COLOR_MAP
  highlight?: boolean
  linkTo?: string
}) {
  const c = COLOR_MAP[color]
  const inner = (
    <div className={[
      'rounded-xl p-5 border transition-all duration-200 h-full',
      highlight
        ? 'border-amber-200 bg-amber-50 shadow-[0_0_0_3px_rgba(251,191,36,0.12)]'
        : 'border-gray-100 bg-white',
    ].join(' ')}>
      <div className="flex items-center justify-between mb-4">
        <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">{label}</p>
        <span className={`w-8 h-8 rounded-lg flex items-center justify-center ${c.icon}`}>
          {icon}
        </span>
      </div>
      <p className={`font-display text-3xl font-extrabold tracking-tight ${c.value}`}>
        {value}
      </p>
    </div>
  )

  if (linkTo) {
    return (
      <Link to={linkTo} className="block hover:-translate-y-0.5 transition-transform duration-150">
        {inner}
      </Link>
    )
  }
  return inner
}

/* Quick action card */
function QuickActionCard({
  title, desc, icon, linkTo, linkLabel, count, countLabel,
}: {
  title: string
  desc: string
  icon: React.ReactNode
  linkTo: string
  linkLabel: string
  count?: number
  countLabel?: string
}) {
  return (
    <div className="bg-white rounded-xl border border-gray-100 p-5">
      <div className="flex items-start gap-4">
        <div className="w-10 h-10 rounded-lg bg-green-50 flex items-center justify-center flex-shrink-0">
          {icon}
        </div>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-1">
            <h3 className="font-display font-bold text-sm text-neutral-900">{title}</h3>
            {count !== undefined && (
              <span className="text-xs font-semibold px-1.5 py-0.5 rounded-full bg-gray-100 text-gray-500">
                {count} {countLabel}
              </span>
            )}
          </div>
          <p className="text-xs text-gray-400 leading-relaxed mb-4">{desc}</p>
          <Link
            to={linkTo}
            className="inline-flex items-center gap-1.5 text-xs font-semibold text-green-700 hover:text-green-500 transition-colors duration-150"
          >
            {linkLabel}
            <ArrowRight size={12} />
          </Link>
        </div>
      </div>
    </div>
  )
}

/* Empty state */
function EmptyState({ message }: { message: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-12 text-center px-5">
      <span className="w-10 h-10 rounded-full bg-gray-50 flex items-center justify-center mb-3">
        <Users size={18} className="text-gray-300" />
      </span>
      <p className="text-sm text-gray-400">{message}</p>
    </div>
  )
}

/* Skeleton */
function AdminSkeleton() {
  return (
    <div className="animate-pulse">
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="bg-gray-50 rounded-xl h-28 border border-gray-100" />
        ))}
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <div className="bg-gray-50 rounded-xl h-32 border border-gray-100" />
        <div className="bg-gray-50 rounded-xl h-32 border border-gray-100" />
      </div>
      <div className="bg-gray-50 rounded-xl h-64 border border-gray-100" />
    </div>
  )
}

/* Helpers */
function formatTime(date: Date): string {
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}