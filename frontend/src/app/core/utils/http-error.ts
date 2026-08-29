import { HttpErrorResponse } from '@angular/common/http';

export function apiErrorMessage(error: unknown, fallback: string): string {
  if (!(error instanceof HttpErrorResponse)) {
    return fallback;
  }
  const body = error.error as { detail?: string; message?: string; title?: string } | string | null;
  if (typeof body === 'string' && body.trim()) {
    return body;
  }
  if (body && typeof body === 'object') {
    const detail = body.detail || body.message;
    if (typeof detail === 'string' && detail.trim()) {
      return detail;
    }
  }
  if (error.status === 401) {
    return 'Session expirée. Reconnecte-toi.';
  }
  if (error.status === 403) {
    return 'Accès refusé.';
  }
  if (error.status === 0) {
    return 'API injoignable. Vérifie que le backend tourne.';
  }
  return fallback;
}
