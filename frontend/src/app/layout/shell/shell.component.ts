import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
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

  readonly nav = computed(() => {
    const items = [
      { path: '/dashboard', label: 'Dashboard' },
      { path: '/servers', label: 'Serveurs' },
      { path: '/triggers', label: 'Triggers' },
      { path: '/logs', label: 'Logs' },
    ];
    if (this.auth.isAdmin()) {
      items.push({ path: '/users', label: 'Comptes' });
    }
    return items;
  });

  logout(): void {
    this.auth.logout();
  }
}
