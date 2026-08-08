import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  DiscordServer,
  DiscordServerRequest,
  HealthStatus,
  Page,
  Trigger,
  TriggerExecution,
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

  getTriggers(): Observable<Trigger[]> {
    return this.http.get<Trigger[]>(`${this.base}/triggers`);
  }

  getTrigger(id: number): Observable<Trigger> {
    return this.http.get<Trigger>(`${this.base}/triggers/${id}`);
  }

  createTrigger(payload: TriggerRequest): Observable<Trigger> {
    return this.http.post<Trigger>(`${this.base}/triggers`, payload);
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

  getLogs(page = 0, size = 50, guildId?: string): Observable<Page<TriggerExecution>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (guildId) {
      params = params.set('guildId', guildId);
    }
    return this.http.get<Page<TriggerExecution>>(`${this.base}/logs`, { params });
  }
}
