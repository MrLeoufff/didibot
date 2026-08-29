import { Routes } from '@angular/router';
import { adminGuard } from './core/guards/admin.guard';
import { authGuard } from './core/guards/auth.guard';
import { ShellComponent } from './layout/shell/shell.component';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'propose',
    loadComponent: () =>
      import('./pages/propose/propose.component').then((m) => m.ProposeComponent),
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
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
      {
        path: 'users',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./pages/users/user-list.component').then((m) => m.UserListComponent),
      },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];
