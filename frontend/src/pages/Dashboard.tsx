import { useDashboard } from '../features/dashboard/useDashboard'
import type { VisitorRow } from '../features/dashboard/dashboardApi'
import {
  Users,
  UserCheck,
  UserMinus,
  AlertTriangle,
  RefreshCw,
  Clock,
  LogOut,
  Loader2,
} from 'lucide-react'

export default function Dashboard() {
  const {
    data,
    isLoading,
    isRefreshing,
    error,
    lastUpdated,
    refresh,
    checkOut,
    checkingOutId,
  } = useDashboard()

  if (isLoading) return <DashboardSkeleton />
  if (error && !data) return <DashboardError message={error} onRetry={refresh} />

  const { summary, activeVisitors, overdueVisitors, recentlyCheckedOut } =
    data!

  return (
    <div className="max-w-7xl mx-auto">
      {/* Page header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="font-display text-2xl font-extrabold text-neutral-900 tracking-tight">
            Dashboard
          </h1>
          {lastUpdated && (
            <p className="text-xs text-gray-400 mt-0.5 flex items-center gap-1">
              <Clock size={11} />
              Updated {formatTime(lastUpdated)}
              <span className="inline-block w-1.5 h-1.5 rounded-full bg-green-400 ml-1 animate-[pulse_2s_ease-in-out_infinite]" />
            </p>
          )}
        </div>

        <button
          onClick={refresh}
          disabled={isRefreshing}
          className="flex items-center gap-2 px-3.5 py-2 rounded-lg border border-gray-200 text-sm font-medium text-gray-600 hover:border-gray-300 hover:text-gray-900 transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed"
          aria-label="Refresh dashboard"
        >
          <RefreshCw
            size={14}
            className={isRefreshing ? 'animate-spin' : ''}
          />
          Refresh
        </button>
      </div>

      {/* Summary cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <SummaryCard
          label="On Premises"
          value={summary.currentlyOnPremises}
          icon={<Users size={18} />}
          color="green"
        />
        <SummaryCard
          label="Checked In Today"
          value={summary.checkedInToday}
          icon={<UserCheck size={18} />}
          color="blue"
        />
        <SummaryCard
          label="Checked Out Today"
          value={summary.checkedOutToday}
          icon={<UserMinus size={18} />}
          color="gray"
        />
        <SummaryCard
          label="Overdue"
          value={summary.overdueCount}
          icon={<AlertTriangle size={18} />}
          color={summary.overdueCount > 0 ? 'amber' : 'gray'}
          highlight={summary.overdueCount > 0}
        />
      </div>

      {/* Active + Overdue */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <VisitorTable
          title="Currently On Premises"
          visitors={activeVisitors}
          emptyMessage="No visitors currently on premises"
          checkOut={checkOut}
          checkingOutId={checkingOutId}
          showCheckout
        />
        <VisitorTable
          title="Overdue Visitors"
          visitors={overdueVisitors}
          emptyMessage="No overdue visitors"
          checkOut={checkOut}
          checkingOutId={checkingOutId}
          showCheckout
          overdue
        />
      </div>

      {/* Recently checked out */}
      <VisitorTable
        title="Recently Checked Out"
        visitors={recentlyCheckedOut}
        emptyMessage="No visitors checked out yet today"
        showCheckout={false}
      />
    </div>
  )
}

/* Summary card */
interface SummaryCardProps {
  label: string
  value: number
  icon: React.ReactNode
  color: 'green' | 'blue' | 'gray' | 'amber'
  highlight?: boolean
}

const COLOR_MAP = {
  green: {
    bg: 'bg-green-50',
    icon: 'bg-green-100 text-green-600',
    value: 'text-green-700',
  },
  blue: {
    bg: 'bg-blue-50',
    icon: 'bg-blue-100 text-blue-600',
    value: 'text-blue-700',
  },
  gray: {
    bg: 'bg-gray-50',
    icon: 'bg-gray-100 text-gray-500',
    value: 'text-gray-700',
  },
  amber: {
    bg: 'bg-amber-50',
    icon: 'bg-amber-100 text-amber-600',
    value: 'text-amber-700',
  },
}

function SummaryCard({ label, value, icon, color, highlight }: SummaryCardProps) {
  const c = COLOR_MAP[color]
  return (
    <div
      className={[
        'rounded-xl p-5 border transition-all duration-200',
        highlight
          ? 'border-amber-200 bg-amber-50 shadow-[0_0_0_3px_rgba(251,191,36,0.12)]'
          : 'border-gray-100 bg-white',
      ].join(' ')}
    >
      <div className="flex items-center justify-between mb-4">
        <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">
          {label}
        </p>
        <span className={`w-8 h-8 rounded-lg flex items-center justify-center ${c.icon}`}>
          {icon}
        </span>
      </div>
      <p className={`font-display text-3xl font-extrabold tracking-tight ${c.value}`}>
        {value}
      </p>
    </div>
  )
}

/* Visitor table */
interface VisitorTableProps {
  title: string
  visitors: VisitorRow[]
  emptyMessage: string
  showCheckout: boolean
  checkOut?: (id: string) => Promise<void>
  checkingOutId?: string | null
  overdue?: boolean
}

function VisitorTable({
  title,
  visitors,
  emptyMessage,
  showCheckout,
  checkOut,
  checkingOutId,
  overdue,
}: VisitorTableProps) {
  return (
    <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
      {/* Table header */}
      <div className="flex items-center justify-between px-5 py-4 border-b border-gray-50">
        <h2 className="font-display font-bold text-sm text-neutral-900 tracking-tight">
          {title}
        </h2>
        <span
          className={[
            'text-xs font-semibold px-2 py-0.5 rounded-full',
            overdue && visitors.length > 0
              ? 'bg-amber-100 text-amber-700'
              : 'bg-gray-100 text-gray-500',
          ].join(' ')}
        >
          {visitors.length}
        </span>
      </div>

      {/* Rows */}
      {visitors.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-12 text-center px-5">
          <span className="w-10 h-10 rounded-full bg-gray-50 flex items-center justify-center mb-3">
            <Users size={18} className="text-gray-300" />
          </span>
          <p className="text-sm text-gray-400">{emptyMessage}</p>
        </div>
      ) : (
        <ul role="list" className="divide-y divide-gray-50">
          {visitors.map((visitor) => (
            <VisitorRow
              key={visitor.id}
              visitor={visitor}
              showCheckout={showCheckout}
              onCheckOut={checkOut}
              isCheckingOut={checkingOutId === visitor.id}
              overdue={overdue}
            />
          ))}
        </ul>
      )}
    </div>
  )
}

/* Visitor row */
interface VisitorRowProps {
  visitor: VisitorRow
  showCheckout: boolean
  onCheckOut?: (id: string) => Promise<void>
  isCheckingOut: boolean
  overdue?: boolean
}

function VisitorRow({
  visitor,
  showCheckout,
  onCheckOut,
  isCheckingOut,
  overdue,
}: VisitorRowProps) {
  const initials = visitor.name
    .split(' ')
    .slice(0, 2)
    .map((n) => n[0]?.toUpperCase() ?? '')
    .join('')

  return (
    <li className="flex items-center gap-3 px-5 py-3.5 hover:bg-gray-50 transition-colors duration-100">
      {/* Avatar */}
      <div
        className={[
          'w-8 h-8 rounded-full flex-shrink-0 flex items-center justify-center text-xs font-bold',
          overdue
            ? 'bg-amber-100 text-amber-700'
            : 'bg-green-100 text-green-700',
        ].join(' ')}
      >
        {initials}
      </div>

      {/* Info */}
      <div className="flex-1 min-w-0">
        <p className="text-sm font-semibold text-neutral-900 truncate">
          {visitor.name}
        </p>
        <p className="text-xs text-gray-400 truncate">
          {visitor.zoneName
            ? `${visitor.zoneName} · `
            : ''}
          {visitor.visitorType} · {formatTime(new Date(visitor.checkInTime))}
        </p>
      </div>

      {/* Overdue badge */}
      {overdue && (
        <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-amber-100 text-amber-700 flex-shrink-0">
          Overdue
        </span>
      )}

      {/* Checkout button */}
      {showCheckout && onCheckOut && (
        <button
          onClick={() => onCheckOut(visitor.id)}
          disabled={isCheckingOut}
          className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold border border-gray-200 text-gray-600 hover:border-red-200 hover:bg-red-50 hover:text-red-600 transition-all duration-150 disabled:opacity-50 disabled:cursor-not-allowed flex-shrink-0"
          aria-label={`Check out ${visitor.name}`}
        >
          {isCheckingOut ? (
            <Loader2 size={12} className="animate-spin" />
          ) : (
            <LogOut size={12} />
          )}
          {isCheckingOut ? 'Checking out…' : 'Check out'}
        </button>
      )}

      {/* Checkout time for recently-checked-out */}
      {!showCheckout && visitor.checkOutTime && (
        <span className="text-xs text-gray-400 flex-shrink-0 flex items-center gap-1">
          <Clock size={11} />
          {formatTime(new Date(visitor.checkOutTime))}
        </span>
      )}
    </li>
  )
}

/* Skeleton */
function DashboardSkeleton() {
  return (
    <div className="max-w-7xl mx-auto animate-pulse">
      <div className="h-8 w-40 bg-gray-100 rounded-lg mb-2" />
      <div className="h-3 w-32 bg-gray-100 rounded mb-8" />
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="bg-gray-50 rounded-xl h-28 border border-gray-100" />
        ))}
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        <div className="bg-gray-50 rounded-xl h-64 border border-gray-100" />
        <div className="bg-gray-50 rounded-xl h-64 border border-gray-100" />
      </div>
      <div className="bg-gray-50 rounded-xl h-48 border border-gray-100" />
    </div>
  )
}

/* Error */
function DashboardError({
  message,
  onRetry,
}: {
  message: string
  onRetry: () => void
}) {
  return (
    <div className="max-w-7xl mx-auto flex flex-col items-center justify-center min-h-[60vh] text-center">
      <span className="w-12 h-12 rounded-full bg-red-50 flex items-center justify-center mb-4">
        <AlertTriangle size={20} className="text-red-400" />
      </span>
      <p className="text-sm font-semibold text-neutral-900 mb-1">
        Failed to load dashboard
      </p>
      <p className="text-sm text-gray-400 mb-6 max-w-xs">{message}</p>
      <button
        onClick={onRetry}
        className="flex items-center gap-2 px-4 py-2 rounded-lg bg-green-700 text-white text-sm font-semibold hover:bg-green-500 transition-colors duration-150"
      >
        <RefreshCw size={14} />
        Try again
      </button>
    </div>
  )
}

/* Helpers */
function formatTime(date: Date): string {
  return date.toLocaleTimeString([], {
    hour: '2-digit',
    minute: '2-digit',
  })
}