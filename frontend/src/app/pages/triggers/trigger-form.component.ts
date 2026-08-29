import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import {
  DiscordServer,
  ResponseRarity,
  Trigger,
  TriggerRequest,
  TriggerType,
} from '../../core/models/api.models';
import { TRIGGER_TYPE_HINTS, TRIGGER_TYPE_LABELS } from '../../core/models/trigger.labels';
import { ApiService } from '../../core/services/api.service';
import { apiErrorMessage } from '../../core/utils/http-error';
import { matchesPattern } from '../../core/utils/pattern-match';
import { isGlobalGuild, serverOptionLabel } from '../../core/utils/server-label';

@Component({
  selector: 'app-trigger-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './trigger-form.component.html',
  styleUrl: './trigger-form.component.scss',
})
export class TriggerFormComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly servers = signal<DiscordServer[]>([]);
  readonly existingTriggers = signal<Trigger[]>([]);
  readonly saving = signal(false);
  readonly copying = signal(false);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly copyMessage = signal<string | null>(null);
  readonly duplicateWarning = signal<string | null>(null);
  readonly testMessage = signal('');
  readonly types: TriggerType[] = ['CONTAINS', 'EXACT', 'STARTS_WITH', 'REGEX'];
  readonly typeLabels = TRIGGER_TYPE_LABELS;
  readonly typeHints = TRIGGER_TYPE_HINTS;
  readonly rarities: { value: ResponseRarity; label: string }[] = [
    { value: 'NORMAL', label: 'Normale' },
    { value: 'RARE', label: 'Rare (~1 %)' },
  ];
  readonly serverOptionLabel = serverOptionLabel;

  editId: number | null = null;

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    pattern: ['', Validators.required],
    type: this.fb.nonNullable.control<TriggerType>('CONTAINS', Validators.required),
    enabled: true,
    cooldownSeconds: [30, [Validators.required, Validators.min(0)]],
    channelScope: this.fb.nonNullable.control<'ALL' | 'INCLUDE' | 'EXCLUDE'>('ALL'),
    discordServerId: this.fb.control<number | null>(null, Validators.required),
    channelIdsText: [''],
    responses: this.fb.nonNullable.array([this.responseGroup()]),
  });

  get responses() {
    return this.form.controls.responses;
  }

  ngOnInit(): void {
    this.form.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.refreshDuplicateWarning());

    this.api.getServers().subscribe({
      next: (servers) => {
        this.servers.set(servers);
        if (!this.editId) {
          this.preselectServer(servers);
        }
      },
      error: (err) => this.error.set(apiErrorMessage(err, 'Impossible de charger les serveurs.')),
    });

    this.api.getTriggers().subscribe({
      next: (triggers) => {
        this.existingTriggers.set(triggers);
        this.refreshDuplicateWarning();
      },
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editId = Number(idParam);
      this.loading.set(true);
      this.api.getTrigger(this.editId).subscribe({
        next: (trigger) => {
          this.responses.clear();
          const items =
            trigger.responses.length > 0
              ? trigger.responses
              : [{ content: '', rarity: 'NORMAL' as ResponseRarity }];
          items.forEach((item) =>
            this.responses.push(this.responseGroup(item.content, item.rarity ?? 'NORMAL'))
          );
          this.form.patchValue({
            name: trigger.name,
            pattern: trigger.pattern,
            type: trigger.type,
            enabled: trigger.enabled,
            cooldownSeconds: trigger.cooldownSeconds,
            channelScope: trigger.channelScope,
            discordServerId: trigger.discordServerId,
            channelIdsText: trigger.channelIds.join(', '),
          });
          this.loading.set(false);
          this.refreshDuplicateWarning();
        },
        error: (err) => {
          this.error.set(apiErrorMessage(err, 'Trigger introuvable.'));
          this.loading.set(false);
        },
      });
    }
  }

  addResponse(): void {
    this.responses.push(this.responseGroup());
  }

  removeResponse(index: number): void {
    if (this.responses.length === 1) {
      return;
    }
    this.responses.removeAt(index);
  }

  invalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  responseInvalid(index: number): boolean {
    const control = this.responses.at(index).get('content');
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  isGlobalServer(): boolean {
    const id = this.form.controls.discordServerId.value;
    if (id == null) {
      return false;
    }
    const server = this.servers().find((item) => item.id === id);
    return !!server && isGlobalGuild(server.discordGuildId);
  }

  matchPreview(): 'empty' | 'yes' | 'no' | 'invalid' {
    const pattern = this.form.controls.pattern.value;
    const type = this.form.controls.type.value;
    const message = this.testMessage();
    if (!pattern.trim() || !message.trim()) {
      return 'empty';
    }
    const result = matchesPattern(type, pattern, message);
    if (result === 'invalid') {
      return 'invalid';
    }
    return result ? 'yes' : 'no';
  }

  onTestInput(event: Event): void {
    this.testMessage.set((event.target as HTMLInputElement).value);
  }

  copyToServers(): void {
    if (!this.editId) {
      return;
    }
    if (
      !confirm(
        'Dupliquer ce trigger sur tous les autres serveurs Discord ? Les serveurs qui ont déjà le même motif seront ignorés.'
      )
    ) {
      return;
    }
    this.copying.set(true);
    this.copyMessage.set(null);
    this.error.set(null);
    this.api.copyTriggerToServers(this.editId).subscribe({
      next: (created) => {
        this.copying.set(false);
        if (created.length === 0) {
          this.copyMessage.set(
            'Aucun serveur à mettre à jour : le motif existe déjà partout, ou il n’y a pas d’autre serveur.'
          );
        } else {
          this.copyMessage.set(
            `Copié sur ${created.length} serveur${created.length > 1 ? 's' : ''}.`
          );
        }
        this.api.getTriggers().subscribe({
          next: (triggers) => this.existingTriggers.set(triggers),
        });
      },
      error: (err) => {
        this.copying.set(false);
        this.error.set(apiErrorMessage(err, 'Duplication impossible.'));
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Complète les champs obligatoires (nom, déclencheur, serveur, au moins une réponse).');
      return;
    }

    const value = this.form.getRawValue();
    const responses = value.responses
      .map((item) => ({
        content: item.content.trim(),
        rarity: item.rarity,
        enabled: true,
      }))
      .filter((item) => item.content);

    if (responses.length === 0) {
      this.error.set('Ajoute au moins une réponse.');
      return;
    }

    const payload: TriggerRequest = {
      name: value.name.trim(),
      pattern: value.pattern.trim(),
      type: value.type,
      enabled: value.enabled,
      cooldownSeconds: Number(value.cooldownSeconds) || 0,
      channelScope: value.channelScope,
      discordServerId: value.discordServerId,
      responses,
      channelIds:
        value.channelScope === 'ALL'
          ? []
          : value.channelIdsText
              .split(',')
              .map((id) => id.trim())
              .filter(Boolean),
    };

    if (payload.channelScope !== 'ALL' && payload.channelIds.length === 0) {
      this.error.set('Indique au moins un ID de salon pour ce scope.');
      return;
    }

    this.saving.set(true);
    this.error.set(null);

    const request = this.editId
      ? this.api.updateTrigger(this.editId, payload)
      : this.api.createTrigger(payload);

    request.subscribe({
      next: () => this.router.navigateByUrl('/triggers'),
      error: (err) => {
        this.error.set(apiErrorMessage(err, 'Enregistrement impossible. Vérifie les champs.'));
        this.saving.set(false);
      },
    });
  }

  private refreshDuplicateWarning(): void {
    const serverId = this.form.controls.discordServerId.value;
    const type = this.form.controls.type.value;
    const pattern = this.form.controls.pattern.value.trim();
    if (!serverId || !pattern) {
      this.duplicateWarning.set(null);
      return;
    }
    const duplicate = this.existingTriggers().find(
      (trigger) =>
        trigger.discordServerId === serverId &&
        trigger.type === type &&
        trigger.pattern.trim().toLowerCase() === pattern.toLowerCase() &&
        trigger.id !== this.editId
    );
    this.duplicateWarning.set(
      duplicate
        ? `Un trigger « ${duplicate.name} » utilise déjà ce motif sur ce serveur.`
        : null
    );
  }

  private responseGroup(content = '', rarity: ResponseRarity = 'NORMAL') {
    return this.fb.nonNullable.group({
      content: [content, Validators.required],
      rarity: this.fb.nonNullable.control<ResponseRarity>(rarity),
    });
  }

  private preselectServer(servers: DiscordServer[]): void {
    const control = this.form.controls.discordServerId;
    if (servers.length === 0) {
      control.clearValidators();
      control.updateValueAndValidity();
      return;
    }
    control.setValidators(Validators.required);
    const preferred =
      servers.find((server) => !isGlobalGuild(server.discordGuildId) && server.enabled) ??
      servers.find((server) => !isGlobalGuild(server.discordGuildId)) ??
      servers[0];
    this.form.patchValue({ discordServerId: preferred.id });
    control.updateValueAndValidity();
  }
}
