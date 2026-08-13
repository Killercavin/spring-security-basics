import { apiClient } from "../../api/client"

export interface Site {
    id: string,
    name: string,
    location: string
}

export interface SiteRequest {
    name: string,
    location: string
}

export interface AdminStats {
    totalSites: number,
    totalUsers: number,
    totalVisitsToday: number,
    totalVisitsAllTime: number,
    currentlyOnPremises: number
}

export const sitesApi = {
    getAll: () => apiClient.get<Site[]>('/api/sites'),
    create: (data: SiteRequest) => apiClient.post<Site>('/api/sites', data),
    update: (id: string, data: SiteRequest) => apiClient.put<Site>(`/api/sites/${id}`, data),
    deactivate: (id: string) => apiClient.delete<void>(`/api/sites/${id}`)
}