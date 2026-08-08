import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Trigger } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-trigger-list',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './trigger-list.component.html',
  styleUrl: './trigger-list.component.scss',
})
export class TriggerListComponent implements OnInit {
  private readonly api = inject(ApiService);

  readonly triggers = signal<Trigger[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.api.getTriggers().subscribe({
      next: (triggers) => {
        this.triggers.set(triggers);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les triggers.');
        this.loading.set(false);
      },
    });
  }

  toggle(trigger: Trigger): void {
    const request = trigger.enabled
      ? this.api.disableTrigger(trigger.id)
      : this.api.enableTrigger(trigger.id);

    request.subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Échec du changement d’état.'),
    });
  }

  remove(trigger: Trigger): void {
    if (!confirm(`Supprimer le trigger « ${trigger.name} » ?`)) {
      return;
    }
    this.api.deleteTrigger(trigger.id).subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Échec de la suppression.'),
    });
  }
}
