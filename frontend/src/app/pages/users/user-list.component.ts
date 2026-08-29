import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { AppUser } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss',
})
export class UserListComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly users = signal<AppUser[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly pending = computed(() => this.users().filter((u) => u.status === 'PENDING'));
  readonly others = computed(() => this.users().filter((u) => u.status !== 'PENDING'));

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.api.getUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les comptes.');
        this.loading.set(false);
      },
    });
  }

  approve(user: AppUser): void {
    this.api.approveUser(user.id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Échec de l’approbation.'),
    });
  }

  reject(user: AppUser): void {
    this.api.rejectUser(user.id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Échec du refus.'),
    });
  }
}
