import { apiClient } from "./client";

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export const authApi = {
  login: (data: LoginRequest) =>
    apiClient.post<AuthResponse>("/api/auth/login", data),

  refresh: (refreshToken: string) =>
    apiClient.post<AuthResponse>("/api/auth/refresh", { refreshToken }),

  logout: (refreshToken: string) =>
    apiClient.post<void>("/api/auth/logout", { refreshToken }),
};