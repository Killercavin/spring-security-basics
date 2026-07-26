import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { authApi } from "../api/auth";
import {
  getEmailFromToken,
  getRoleFromToken,
  getUserIdFromToken,
  isTokenExpired,
} from "./jwt";
import { tokenStorage } from "./tokenStorage";

export type Role = "SUPER_ADMIN" | "MANAGER" | "STAFF";

export interface AuthUser {
  userId: string;
  email: string;
  role: Role;
}

interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

interface AuthContextValue extends AuthState {
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  isRole: (...roles: Role[]) => boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

function buildUser(accessToken: string): AuthUser {
  return {
    userId: getUserIdFromToken(accessToken),
    email: getEmailFromToken(accessToken),
    role: getRoleFromToken(accessToken) as Role,
  };
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [state, setState] = useState<AuthState>(() => {
    const accessToken = tokenStorage.getAccess();
    const refreshToken = tokenStorage.getRefresh();

    if (!accessToken || !refreshToken) {
      return { user: null, isAuthenticated: false, isLoading: false };
    }

    if (!isTokenExpired(accessToken)) {
      return {
        user: buildUser(accessToken),
        isAuthenticated: true,
        isLoading: false,
      };
    }

    return { user: null, isAuthenticated: false, isLoading: true };
  });

  // Bootstrap — restore session from stored token
  useEffect(() => {
    const accessToken = tokenStorage.getAccess();
    const refreshToken = tokenStorage.getRefresh();

    if (!accessToken || !refreshToken) return;

    if (!isTokenExpired(accessToken)) {
      return;
    }

    // access token expired — try silent refresh
    authApi
      .refresh(refreshToken)
      .then(({ data }) => {
        tokenStorage.set(data.accessToken, data.refreshToken);
        setState({
          user: buildUser(data.accessToken),
          isAuthenticated: true,
          isLoading: false,
        });
      })
      .catch(() => {
        tokenStorage.clear();
        setState({ user: null, isAuthenticated: false, isLoading: false });
      });
  }, []);

  // Listen for session expiry from axios interceptor
  useEffect(() => {
    const onExpired = () => {
      setState({ user: null, isAuthenticated: false, isLoading: false });
    };
    window.addEventListener("gatelog:session-expired", onExpired);
    return () => window.removeEventListener("gatelog:session-expired", onExpired);
  }, []);

  // Login
  const login = useCallback(async (email: string, password: string) => {
    const { data } = await authApi.login({ email, password });
    tokenStorage.set(data.accessToken, data.refreshToken);
    setState({
      user: buildUser(data.accessToken),
      isAuthenticated: true,
      isLoading: false,
    });
  }, []);

  // Logout
  const logout = useCallback(async () => {
    const refreshToken = tokenStorage.getRefresh();
    try {
      if (refreshToken) await authApi.logout(refreshToken);
    } catch {
      // server-side logout failure is non-critical — clear locally regardless
    } finally {
      tokenStorage.clear();
      setState({ user: null, isAuthenticated: false, isLoading: false });
    }
  }, []);

  // Role helper
  const isRole = useCallback(
    (...roles: Role[]) => {
      return state.user ? roles.includes(state.user.role) : false;
    },
    [state.user]
  );

  const value = useMemo(
    () => ({ ...state, login, logout, isRole }),
    [state, login, logout, isRole]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}