import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();
  const skipAuth =
    req.url.includes('/api/auth/login') ||
    req.url.includes('/api/auth/register') ||
    req.url.includes('/api/triggers/propose');
  const attachToken = !!token && !skipAuth;

  const authedReq = attachToken
    ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      })
    : req;

  return next(authedReq).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 && attachToken) {
        auth.logout();
      }
      return throwError(() => err);
    })
  );
};
