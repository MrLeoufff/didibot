import { Routes } from '@angular/router';
import { ShellComponent } from './layout/shell/shell.component';

export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./pages/dashboard/dashboard.component').then((m) => m.DashboardComponent),
      },
      {
        path: 'triggers',
        loadComponent: () =>
          import('./pages/triggers/trigger-list.component').then((m) => m.TriggerListComponent),
      },
      {
        path: 'triggers/new',
        loadComponent: () =>
          import('./pages/triggers/trigger-form.component').then((m) => m.TriggerFormComponent),
      },
      {
        path: 'triggers/:id',
        loadComponent: () =>
          import('./pages/triggers/trigger-form.component').then((m) => m.TriggerFormComponent),
      },
      {
        path: 'servers',
        loadComponent: () =>
          import('./pages/servers/servers.component').then((m) => m.ServersComponent),
      },
      {
        path: 'logs',
        loadComponent: () => import('./pages/logs/logs.component').then((m) => m.LogsComponent),
      },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
