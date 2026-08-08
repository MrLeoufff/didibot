import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { HealthStatus, Trigger, TriggerExecution } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, DatePipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly loading = signal(true);
  readonly health = signal<HealthStatus | null>(null);
  readonly triggers = signal<Trigger[]>([]);
  readonly recentLogs = signal<TriggerExecution[]>([]);
  readonly serverCount = signal(0);
  readonly error = signal<string | null>(null);
  readonly activeTriggers = computed(() => this.triggers().filter((t) => t.enabled).length);

  ngOnInit(): void {
    forkJoin({
      health: this.api.getHealth(),
      triggers: this.api.getTriggers(),
      logs: this.api.getLogs(0, 8),
      servers: this.api.getServers(),
    }).subscribe({
      next: ({ health, triggers, logs, servers }) => {
        this.health.set(health);
        this.triggers.set(triggers);
        this.recentLogs.set(logs.content ?? []);
        this.serverCount.set(servers.length);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger le dashboard. Vérifie que l’API est disponible.');
        this.loading.set(false);
      },
    });
  }
}
