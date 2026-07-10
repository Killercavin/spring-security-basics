export interface JwtPayload {
  sub: string;       // userId
  email: string;
  role: string;
  iat: number;
  exp: number;
}

export function parseJwt(token: string): JwtPayload {
  const payload = token.split(".")[1];
  const decoded = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
  return JSON.parse(decoded);
}

export function isTokenExpired(token: string): boolean {
  try {
    const { exp } = parseJwt(token);
    // treat as expired 30 seconds early to avoid edge cases
    return Date.now() / 1000 >= exp - 30;
  } catch {
    return true;
  }
}

export function getRoleFromToken(token: string): string {
  return parseJwt(token).role;
}

export function getUserIdFromToken(token: string): string {
  return parseJwt(token).sub;
}

export function getEmailFromToken(token: string): string {
  return parseJwt(token).email;
}