import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AppUser,
  DiscordServer,
  DiscordServerRequest,
  HealthStatus,
  Page,
  RegisterRequest,
  Trigger,
  TriggerExecution,
  TriggerProposeRequest,
  TriggerRequest,
  TriggerResponse,
} from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api';

  getHealth(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>(`${this.base}/health`);
  }

  register(payload: RegisterRequest): Observable<AppUser> {
    return this.http.post<AppUser>(`${this.base}/auth/register`, payload);
  }

  getUsers(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(`${this.base}/users`);
  }

  getPendingUsers(): Observable<AppUser[]> {
    return this.http.get<AppUser[]>(`${this.base}/users/pending`);
  }

  approveUser(id: number): Observable<AppUser> {
    return this.http.patch<AppUser>(`${this.base}/users/${id}/approve`, {});
  }

  rejectUser(id: number): Observable<AppUser> {
    return this.http.patch<AppUser>(`${this.base}/users/${id}/reject`, {});
  }

  getTriggers(): Observable<Trigger[]> {
    return this.http.get<Trigger[]>(`${this.base}/triggers`);
  }

  getPendingTriggers(): Observable<Trigger[]> {
    return this.http.get<Trigger[]>(`${this.base}/triggers/pending`);
  }

  getTrigger(id: number): Observable<Trigger> {
    return this.http.get<Trigger>(`${this.base}/triggers/${id}`);
  }

  createTrigger(payload: TriggerRequest): Observable<Trigger> {
    return this.http.post<Trigger>(`${this.base}/triggers`, payload);
  }

  proposeTrigger(payload: TriggerProposeRequest): Observable<Trigger> {
    return this.http.post<Trigger>(`${this.base}/triggers/propose`, payload);
  }

  updateTrigger(id: number, payload: TriggerRequest): Observable<Trigger> {
    return this.http.put<Trigger>(`${this.base}/triggers/${id}`, payload);
  }

  deleteTrigger(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/triggers/${id}`);
  }

  enableTrigger(id: number): Observable<Trigger> {
    return this.http.patch<Trigger>(`${this.base}/triggers/${id}/enable`, {});
  }

  disableTrigger(id: number): Observable<Trigger> {
    return this.http.patch<Trigger>(`${this.base}/triggers/${id}/disable`, {});
  }

  approveTrigger(id: number): Observable<Trigger> {
    return this.http.patch<Trigger>(`${this.base}/triggers/${id}/approve`, {});
  }

  rejectTrigger(id: number): Observable<Trigger> {
    return this.http.patch<Trigger>(`${this.base}/triggers/${id}/reject`, {});
  }

  copyTriggerToServers(id: number): Observable<Trigger[]> {
    return this.http.post<Trigger[]>(`${this.base}/triggers/${id}/copy-to-servers`, {});
  }

  addResponse(triggerId: number, content: string): Observable<TriggerResponse> {
    return this.http.post<TriggerResponse>(`${this.base}/triggers/${triggerId}/responses`, {
      content,
      enabled: true,
    });
  }

  deleteResponse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/responses/${id}`);
  }

  getServers(): Observable<DiscordServer[]> {
    return this.http.get<DiscordServer[]>(`${this.base}/servers`);
  }

  createServer(payload: DiscordServerRequest): Observable<DiscordServer> {
    return this.http.post<DiscordServer>(`${this.base}/servers`, payload);
  }

  updateServer(id: number, payload: DiscordServerRequest): Observable<DiscordServer> {
    return this.http.put<DiscordServer>(`${this.base}/servers/${id}`, payload);
  }

  enableServer(id: number): Observable<DiscordServer> {
    return this.http.patch<DiscordServer>(`${this.base}/servers/${id}/enable`, {});
  }

  disableServer(id: number): Observable<DiscordServer> {
    return this.http.patch<DiscordServer>(`${this.base}/servers/${id}/disable`, {});
  }

  getLogs(page = 0, size = 25, guildId?: string, q?: string): Observable<Page<TriggerExecution>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size)
      .set('sort', 'executedAt,desc');
    if (guildId) {
      params = params.set('guildId', guildId);
    }
    if (q && q.trim()) {
      params = params.set('q', q.trim());
    }
    return this.http.get<Page<TriggerExecution>>(`${this.base}/logs`, { params });
  }
}
