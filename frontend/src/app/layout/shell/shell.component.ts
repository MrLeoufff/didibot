import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';

const TAGLINES = [
  'Java > C#',
  'Troll as a Service',
  'La JVM observe',
  '1 % de légendaire',
  'Café. Compile. Troll.',
];

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
  readonly tagline = TAGLINES[Math.floor(Math.random() * TAGLINES.length)];

  readonly nav = computed(() => {
    const pending = this.pendingCount();
    const items = [
      { path: '/dashboard', label: 'QG', badge: 0 },
      { path: '/servers', label: 'Territoires', badge: 0 },
      { path: '/triggers', label: 'Punchlines', badge: pending },
      { path: '/logs', label: 'Preuves', badge: 0 },
      { path: '/settings', label: 'Réglages', badge: 0 },
    ];
    if (this.auth.isAdmin()) {
      items.push({ path: '/users', label: 'Gardiens', badge: 0 });
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
