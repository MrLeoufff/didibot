import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {
  readonly nav = [
    { path: '/dashboard', label: 'Dashboard' },
    { path: '/servers', label: 'Serveurs' },
    { path: '/triggers', label: 'Triggers' },
    { path: '/logs', label: 'Logs' },
  ];
}
