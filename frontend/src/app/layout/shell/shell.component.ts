import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {
  readonly auth = inject(AuthService);
  private readonly api = inject(ApiService);

  readonly pendingCount = signal(0);

  readonly nav = computed(() => {
    const pending = this.pendingCount();
    const items = [
      { path: '/dashboard', label: 'Dashboard', badge: 0 },
      { path: '/servers', label: 'Serveurs', badge: 0 },
      { path: '/triggers', label: 'Triggers', badge: pending },
      { path: '/logs', label: 'Logs', badge: 0 },
    ];
    if (this.auth.isAdmin()) {
      items.push({ path: '/users', label: 'Comptes', badge: 0 });
    }
    return items;
  });

  constructor() {
    this.api.getPendingTriggers().subscribe({
      next: (list) => this.pendingCount.set(list.length),
      error: () => this.pendingCount.set(0),
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
