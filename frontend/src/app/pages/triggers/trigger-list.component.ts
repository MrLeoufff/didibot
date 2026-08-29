import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Trigger, TriggerStatus } from '../../core/models/api.models';
import { TRIGGER_STATUS_LABELS, TRIGGER_TYPE_LABELS } from '../../core/models/trigger.labels';
import { ApiService } from '../../core/services/api.service';
import { apiErrorMessage } from '../../core/utils/http-error';

type StatusFilter = 'ALL' | TriggerStatus | 'ACTIVE' | 'INACTIVE';

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
  readonly query = signal('');
  readonly statusFilter = signal<StatusFilter>('ALL');

  readonly typeLabels = TRIGGER_TYPE_LABELS;
  readonly statusLabels = TRIGGER_STATUS_LABELS;

  readonly pending = computed(() => this.triggers().filter((t) => t.status === 'PENDING'));

  readonly filtered = computed(() => {
    const q = this.query().trim().toLowerCase();
    const filter = this.statusFilter();
    return this.triggers()
      .filter((trigger) => trigger.status !== 'PENDING')
      .filter((trigger) => {
        if (filter === 'ACTIVE') return trigger.enabled;
        if (filter === 'INACTIVE') return !trigger.enabled;
        if (filter === 'APPROVED' || filter === 'REJECTED') return trigger.status === filter;
        return true;
      })
      .filter((trigger) => {
        if (!q) return true;
        return (
          trigger.name.toLowerCase().includes(q) ||
          trigger.pattern.toLowerCase().includes(q) ||
          trigger.discordServerName.toLowerCase().includes(q) ||
          trigger.type.toLowerCase().includes(q)
        );
      });
  });

  ngOnInit(): void {
    this.reload();
  }

  onSearch(event: Event): void {
    this.query.set((event.target as HTMLInputElement).value);
  }

  reload(): void {
    this.loading.set(true);
    this.api.getTriggers().subscribe({
      next: (triggers) => {
        this.triggers.set(triggers);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(apiErrorMessage(err, 'Impossible de charger les triggers.'));
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
      error: (err) => this.error.set(apiErrorMessage(err, 'Échec du changement d’état.')),
    });
  }

  approve(trigger: Trigger): void {
    this.api.approveTrigger(trigger.id).subscribe({
      next: () => this.reload(),
      error: (err) => this.error.set(apiErrorMessage(err, 'Échec de l’approbation.')),
    });
  }

  reject(trigger: Trigger): void {
    this.api.rejectTrigger(trigger.id).subscribe({
      next: () => this.reload(),
      error: (err) => this.error.set(apiErrorMessage(err, 'Échec du refus.')),
    });
  }

  remove(trigger: Trigger): void {
    if (!confirm(`Supprimer le trigger « ${trigger.name} » ?`)) {
      return;
    }
    this.api.deleteTrigger(trigger.id).subscribe({
      next: () => this.reload(),
      error: (err) => this.error.set(apiErrorMessage(err, 'Échec de la suppression.')),
    });
  }
}
