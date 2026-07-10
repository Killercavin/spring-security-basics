import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";
import { tokenStorage } from "../auth/tokenStorage";

const BASE_URL = import.meta.env.VITE_API_URL;

export const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 15_000,
});

// ── Request interceptor — inject access token ──────────────────────────────
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = tokenStorage.getAccess();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ── Response interceptor — silent token refresh on 401 ────────────────────
let isRefreshing = false;
let refreshQueue: Array<{
  resolve: (token: string) => void;
  reject: (err: unknown) => void;
}> = [];

function processQueue(error: unknown, token: string | null) {
  refreshQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error);
    else if (token) resolve(token);
  });
  refreshQueue = [];
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const original = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean;
    };

    // only attempt refresh on 401, and not on the refresh endpoint itself
    const is401 = error.response?.status === 401;
    const isRefreshEndpoint = original.url?.includes("/auth/refresh");

    if (!is401 || isRefreshEndpoint || original._retry) {
      return Promise.reject(error);
    }

    if (isRefreshing) {
      // queue this request until the refresh completes
      return new Promise((resolve, reject) => {
        refreshQueue.push({ resolve, reject });
      }).then((token) => {
        original.headers.Authorization = `Bearer ${token}`;
        return apiClient(original);
      });
    }

    original._retry = true;
    isRefreshing = true;

    try {
      const refreshToken = tokenStorage.getRefresh();
      if (!refreshToken) throw new Error("No refresh token");

      const { data } = await axios.post<{
        accessToken: string;
        refreshToken: string;
      }>(`${BASE_URL}/api/auth/refresh`, { refreshToken });

      tokenStorage.set(data.accessToken, data.refreshToken);
      processQueue(null, data.accessToken);

      original.headers.Authorization = `Bearer ${data.accessToken}`;
      return apiClient(original);
    } catch (refreshError) {
      processQueue(refreshError, null);
      tokenStorage.clear();
      // signal the auth context to reset state
      window.dispatchEvent(new Event("gatelog:session-expired"));
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }
);