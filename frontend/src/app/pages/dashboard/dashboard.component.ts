import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { BotStats, HealthStatus, TriggerExecution } from '../../core/models/api.models';
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
  readonly stats = signal<BotStats | null>(null);
  readonly recentLogs = signal<TriggerExecution[]>([]);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    forkJoin({
      health: this.api.getHealth(),
      stats: this.api.getStats(),
      logs: this.api.getLogs(0, 8),
    }).subscribe({
      next: ({ health, stats, logs }) => {
        this.health.set(health);
        this.stats.set(stats);
        this.recentLogs.set(logs.content ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Le QG est hors-ligne. Vérifie que l’API répond encore.');
        this.loading.set(false);
      },
    });
  }
}
