export function readJwtPayload(token: string): { exp?: number; sub?: string; role?: string } | null {
  try {
    const part = token.split('.')[1];
    if (!part) {
      return null;
    }
    const padded = part.replace(/-/g, '+').replace(/_/g, '/') + '='.repeat((4 - (part.length % 4)) % 4);
    return JSON.parse(atob(padded));
  } catch {
    return null;
  }
}

export function isJwtUsable(token: string | null | undefined): boolean {
  if (!token) {
    return false;
  }
  const payload = readJwtPayload(token);
  if (!payload?.exp) {
    return false;
  }
  return payload.exp * 1000 > Date.now() + 5_000;
}
