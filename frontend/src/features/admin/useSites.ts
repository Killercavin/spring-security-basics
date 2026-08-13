import { useCallback, useEffect, useRef, useState } from "react";
import { sitesApi, type Site, type SiteRequest } from "./adminApi";

interface UseSitesResponse {
    sites: Site[],
    isLoading: boolean,
    error: string | null,
    createSite: (data: SiteRequest) => Promise<void>,
    updateSite: (id: string, data: SiteRequest) => Promise<void>,
    deactivateSite: (id: string) => Promise<void>,
    refresh: () => Promise<void>
}

export function useSites(): UseSitesResponse {
    const [sites, setSites] = useState<Site[]>([])
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const isMountedRef = useRef(true)

    const fetchSites = useRef(async () => {
        setIsLoading(true)
        try {
            const { data } = await sitesApi.getAll()
            if (!isMountedRef.current) return
            setSites(data)
            setError(null)
        } catch {
            if (!isMountedRef.current) return
            setError('Failed to load sites')
        } finally {
            if (isMountedRef.current) setIsLoading(false)
        }
    }).current

    useEffect(() => {
        isMountedRef.current = true
        fetchSites()
        return () => { isMountedRef.current = false }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const createSite = useCallback(async (data: SiteRequest) => {
        const { data: created } = await sitesApi.create(data)
        setSites((prev) => [...prev, created])
    }, [])

    const updateSite = useCallback(async (id: string, data: SiteRequest) => {
        const { data: updated } = await sitesApi.update(id, data)
        setSites((prev) => prev.map((s) => (s.id === id ? updated : s)))
    }, [])

    const deactivateSite = useCallback(async (id: string) => {
        await sitesApi.deactivate(id)
        setSites((prev) => prev.filter((s) => s.id !== id))
    }, [])

    const refresh = useCallback(async () => {
        await fetchSites()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    return { sites, isLoading, error, createSite, updateSite, deactivateSite, refresh }
}