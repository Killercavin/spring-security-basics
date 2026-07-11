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