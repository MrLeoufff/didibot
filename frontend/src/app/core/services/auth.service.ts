import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginResponse } from '../models/api.models';
import { isJwtUsable } from '../utils/jwt';

const TOKEN_KEY = 'didibot_admin_token';
const USER_KEY = 'didibot_admin_user';
const ROLE_KEY = 'didibot_admin_role';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly tokenSignal = signal<string | null>(null);
  private readonly userSignal = signal<string | null>(null);
  private readonly roleSignal = signal<string | null>(null);

  readonly token = this.tokenSignal.asReadonly();
  readonly username = this.userSignal.asReadonly();
  readonly role = this.roleSignal.asReadonly();
  readonly isAuthenticated = computed(() => this.hasValidSession());
  readonly isAdmin = computed(() => this.roleSignal() === 'ADMIN' && this.hasValidSession());

  constructor() {
    this.restoreSession();
  }

  hasValidSession(): boolean {
    return isJwtUsable(this.tokenSignal());
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/api/auth/login', { username, password }).pipe(
      tap((response) => {
        localStorage.setItem(TOKEN_KEY, response.token);
        localStorage.setItem(USER_KEY, response.username);
        localStorage.setItem(ROLE_KEY, response.role);
        this.tokenSignal.set(response.token);
        this.userSignal.set(response.username);
        this.roleSignal.set(response.role);
      })
    );
  }

  logout(): void {
    this.clearSession();
    this.router.navigateByUrl('/login');
  }

  private restoreSession(): void {
    const token = localStorage.getItem(TOKEN_KEY);
    if (!isJwtUsable(token)) {
      this.clearSession();
      return;
    }
    this.tokenSignal.set(token);
    this.userSignal.set(localStorage.getItem(USER_KEY));
    this.roleSignal.set(localStorage.getItem(ROLE_KEY));
  }

  private clearSession(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(ROLE_KEY);
    this.tokenSignal.set(null);
    this.userSignal.set(null);
    this.roleSignal.set(null);
  }
}
