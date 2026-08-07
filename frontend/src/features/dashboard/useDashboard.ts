import { useCallback, useEffect, useRef, useState } from 'react'
import { dashboardApi, type DashboardFeed } from './dashboardApi'

const POLL_INTERVAL_MS = 30_000

interface UseDashboardReturn {
  data: DashboardFeed | null
  isLoading: boolean
  isRefreshing: boolean
  error: string | null
  lastUpdated: Date | null
  refresh: () => Promise<void>
  checkOut: (visitorId: string) => Promise<void>
  checkingOutId: string | null
}

export function useDashboard(): UseDashboardReturn {
  const [data, setData]               = useState<DashboardFeed | null>(null)
  const [isLoading, setIsLoading]     = useState(true)
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [error, setError]             = useState<string | null>(null)
  const [lastUpdated, setLastUpdated] = useState<Date | null>(null)
  const [checkingOutId, setCheckingOutId] = useState<string | null>(null)

  const isMountedRef  = useRef(true)
  const dataRef       = useRef<DashboardFeed | null>(null)
  const intervalRef   = useRef<ReturnType<typeof setInterval> | null>(null)

  // keep dataRef in sync so fetchData can read latest data
  // without being a dependency of fetchData itself
  useEffect(() => {
    dataRef.current = data
  }, [data])

  // stable fetch - never recreated, no dependency on data state
  const fetchData = useRef(async (silent = false) => {
    if (!isMountedRef.current) return

    if (!silent) {
      if (dataRef.current) setIsRefreshing(true)
      else setIsLoading(true)
    }

    try {
      const { data: feed } = await dashboardApi.getFeed()
      if (!isMountedRef.current) return
      setData(feed)
      setLastUpdated(new Date())
      setError(null)
    } catch {
      if (!isMountedRef.current) return
      if (!silent) {
        setError('Failed to load dashboard. Check your connection.')
      }
      // on silent failure keep stale data - do not wipe the dashboard
    } finally {
      if (isMountedRef.current) {
        setIsLoading(false)
        setIsRefreshing(false)
      }
    }
  }).current

  // initial load - runs once
  useEffect(() => {
    isMountedRef.current = true
    fetchData(false)
    return () => { isMountedRef.current = false }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // auto-poll - stable interval, never reset by data changes
  useEffect(() => {
    intervalRef.current = setInterval(() => fetchData(true), POLL_INTERVAL_MS)
    return () => {
      if (intervalRef.current) clearInterval(intervalRef.current)
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  // refresh tab on visibility change
  useEffect(() => {
    const onVisibility = () => {
      if (document.visibilityState === 'visible') fetchData(true)
    }
    document.addEventListener('visibilitychange', onVisibility)
    return () => document.removeEventListener('visibilitychange', onVisibility)
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const refresh = useCallback(async () => {
    await fetchData(false)
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const checkOut = useCallback(async (visitorId: string) => {
    setCheckingOutId(visitorId)
    try {
      await dashboardApi.checkOut(visitorId)
      setData((prev) => {
        if (!prev) return prev
        const wasActive  = prev.activeVisitors.find((v) => v.id === visitorId)
        const wasOverdue = prev.overdueVisitors.find((v) => v.id === visitorId)
        const checkedOut = wasActive ?? wasOverdue
        if (!checkedOut) return prev

        const updated = {
          ...checkedOut,
          status: 'CHECKED_OUT',
          checkOutTime: new Date().toISOString(),
        }

        return {
          ...prev,
          summary: {
            ...prev.summary,
            currentlyOnPremises: Math.max(0, prev.summary.currentlyOnPremises - 1),
            checkedOutToday: prev.summary.checkedOutToday + 1,
            overdueCount: wasOverdue
              ? Math.max(0, prev.summary.overdueCount - 1)
              : prev.summary.overdueCount,
          },
          activeVisitors:     prev.activeVisitors.filter((v) => v.id !== visitorId),
          overdueVisitors:    prev.overdueVisitors.filter((v) => v.id !== visitorId),
          recentlyCheckedOut: [updated, ...prev.recentlyCheckedOut].slice(0, 10),
        }
      })
    } catch {
      await fetchData(true)
    } finally {
      setCheckingOutId(null)
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  return {
    data,
    isLoading,
    isRefreshing,
    error,
    lastUpdated,
    refresh,
    checkOut,
    checkingOutId,
  }
}