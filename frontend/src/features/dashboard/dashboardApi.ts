import { apiClient } from '../../api/client'

export interface VisitorSummary {
  currentlyOnPremises: number
  checkedInToday: number
  checkedOutToday: number
  overdueCount: number
  asOf: string
}

export interface VisitorRow {
  id: string
  name: string
  phone: string
  visitorType: string
  purpose: string
  status: string
  siteId: string
  zoneId: string | null
  zoneName: string | null
  hostId: string | null
  hostName: string | null
  createdById: string
  createdByName: string
  checkInTime: string
  checkOutTime: string | null
}

export interface DashboardFeed {
  summary: VisitorSummary
  activeVisitors: VisitorRow[]
  overdueVisitors: VisitorRow[]
  recentlyCheckedOut: VisitorRow[]
}

export const dashboardApi = {
  getFeed: () =>
    apiClient.get<DashboardFeed>('/api/dashboard'),

  checkOut: (visitorId: string) =>
    apiClient.patch<VisitorRow>(`/api/visitors/${visitorId}/checkout`),
}