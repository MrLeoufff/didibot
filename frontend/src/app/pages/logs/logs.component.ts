import { DatePipe } from '@angular/common';
import { Component, OnDestroy, OnInit, inject, signal } from '@angular/core';
import { DiscordServer, TriggerExecution } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';
import { isGlobalGuild, serverDisplayName } from '../../core/utils/server-label';

@Component({
  selector: 'app-logs',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './logs.component.html',
  styleUrl: './logs.component.scss',
})
export class LogsComponent implements OnInit, OnDestroy {
  private readonly api = inject(ApiService);
  private searchTimer: ReturnType<typeof setTimeout> | undefined;

  readonly logs = signal<TriggerExecution[]>([]);
  readonly servers = signal<DiscordServer[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly total = signal(0);
  readonly totalPages = signal(0);
  readonly page = signal(0);
  readonly size = 25;
  readonly guildId = signal('');
  readonly query = signal('');

  ngOnInit(): void {
    this.api.getServers().subscribe({
      next: (servers) => this.servers.set(servers),
    });
    this.load();
  }

  ngOnDestroy(): void {
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
  }

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
    this.searchTimer = setTimeout(() => {
      this.query.set(value);
      this.page.set(0);
      this.load();
    }, 300);
  }

  onGuildChange(event: Event): void {
    this.guildId.set((event.target as HTMLSelectElement).value);
    this.page.set(0);
    this.load();
  }

  prev(): void {
    if (this.page() === 0) {
      return;
    }
    this.page.update((page) => page - 1);
    this.load();
  }

  next(): void {
    if (this.page() + 1 >= this.totalPages()) {
      return;
    }
    this.page.update((page) => page + 1);
    this.load();
  }

  serverName(guildId: string): string {
    const server = this.servers().find((item) => item.discordGuildId === guildId);
    if (!server) {
      return isGlobalGuild(guildId) ? 'Global — tous les serveurs' : guildId;
    }
    return serverDisplayName(server.name, server.discordGuildId);
  }

  private load(): void {
    this.loading.set(true);
    this.error.set(null);
    this.api.getLogs(this.page(), this.size, this.guildId() || undefined, this.query()).subscribe({
      next: (result) => {
        this.logs.set(result.content ?? []);
        this.total.set(result.totalElements ?? 0);
        this.totalPages.set(result.totalPages ?? 0);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les logs.');
        this.loading.set(false);
      },
    });
  }
}
