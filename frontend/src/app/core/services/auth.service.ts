import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginResponse } from '../models/api.models';

const TOKEN_KEY = 'didibot_admin_token';
const USER_KEY = 'didibot_admin_user';
const ROLE_KEY = 'didibot_admin_role';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  private readonly tokenSignal = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  private readonly userSignal = signal<string | null>(localStorage.getItem(USER_KEY));
  private readonly roleSignal = signal<string | null>(localStorage.getItem(ROLE_KEY));

  readonly token = this.tokenSignal.asReadonly();
  readonly username = this.userSignal.asReadonly();
  readonly role = this.roleSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.tokenSignal());
  readonly isAdmin = computed(() => this.roleSignal() === 'ADMIN');

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
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    localStorage.removeItem(ROLE_KEY);
    this.tokenSignal.set(null);
    this.userSignal.set(null);
    this.roleSignal.set(null);
    this.router.navigateByUrl('/login');
  }
}
