import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { TriggerExecution } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-logs',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './logs.component.html',
  styleUrl: './logs.component.scss',
})
export class LogsComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly logs = signal<TriggerExecution[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly total = signal(0);

  ngOnInit(): void {
    this.api.getLogs(0, 100).subscribe({
      next: (page) => {
        this.logs.set(page.content ?? []);
        this.total.set(page.totalElements ?? 0);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les logs.');
        this.loading.set(false);
      },
    });
  }
}
