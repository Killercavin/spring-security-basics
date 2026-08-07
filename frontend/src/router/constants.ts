import { type Role } from "../auth/AuthContext";

export const ROUTE_PATHS = {
  ROOT:         "/",
  LOGIN:        "/login",
  UNAUTHORIZED: "/unauthorized",
  ADMIN:        "/admin",
  DASHBOARD:    "/dashboard",
  VISITORS:     "/visitors",
  NEW_VISITOR:  "/visitors/new",
  REPORTS:      "/reports",
  USERS:        "/users",
  SITES:        "/sites",
} as const;

export const ROLE_DESTINATION_MAP: Record<Role, string> = {
  SUPER_ADMIN: ROUTE_PATHS.ADMIN,
  MANAGER:     ROUTE_PATHS.DASHBOARD,
  STAFF:       ROUTE_PATHS.NEW_VISITOR,
};

export function getRoleDestination(role: Role | undefined): string {
  return role && role in ROLE_DESTINATION_MAP
    ? ROLE_DESTINATION_MAP[role]
    : ROUTE_PATHS.LOGIN;
}

export function isPathAllowedForRole(path: string, role: Role): boolean {
  const staffPaths    = [ROUTE_PATHS.VISITORS, ROUTE_PATHS.NEW_VISITOR]
  const managerPaths  = [...staffPaths, ROUTE_PATHS.DASHBOARD, ROUTE_PATHS.REPORTS, ROUTE_PATHS.USERS]
  const adminPaths    = [...managerPaths, ROUTE_PATHS.ADMIN, ROUTE_PATHS.SITES]

  switch (role) {
    case 'STAFF':       return staffPaths.some(p => path.startsWith(p))
    case 'MANAGER':     return managerPaths.some(p => path.startsWith(p))
    case 'SUPER_ADMIN': return adminPaths.some(p => path.startsWith(p))
    default:            return false
  }
}