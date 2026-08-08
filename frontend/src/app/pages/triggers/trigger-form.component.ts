import { Component, OnInit, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DiscordServer, TriggerRequest, TriggerType } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';

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

  readonly servers = signal<DiscordServer[]>([]);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly types: TriggerType[] = ['CONTAINS', 'EXACT', 'STARTS_WITH', 'REGEX'];

  editId: number | null = null;

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    pattern: ['', Validators.required],
    type: this.fb.nonNullable.control<TriggerType>('CONTAINS', Validators.required),
    enabled: true,
    cooldownSeconds: [30, [Validators.required, Validators.min(0)]],
    channelScope: this.fb.nonNullable.control<'ALL' | 'INCLUDE' | 'EXCLUDE'>('ALL'),
    discordServerId: this.fb.control<number | null>(null),
    channelIdsText: [''],
    responses: this.fb.nonNullable.array([this.fb.nonNullable.control('', Validators.required)]),
  });

  get responses(): FormArray {
    return this.form.controls.responses;
  }

  ngOnInit(): void {
    this.api.getServers().subscribe({
      next: (servers) => this.servers.set(servers),
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.editId = Number(idParam);
      this.api.getTrigger(this.editId).subscribe({
        next: (trigger) => {
          this.responses.clear();
          const contents =
            trigger.responses.length > 0 ? trigger.responses.map((r) => r.content) : [''];
          contents.forEach((content) =>
            this.responses.push(this.fb.nonNullable.control(content, Validators.required))
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
        },
        error: () => this.error.set('Trigger introuvable.'),
      });
    }
  }

  addResponse(): void {
    this.responses.push(this.fb.nonNullable.control('', Validators.required));
  }

  removeResponse(index: number): void {
    if (this.responses.length === 1) {
      return;
    }
    this.responses.removeAt(index);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.getRawValue();
    const payload: TriggerRequest = {
      name: value.name.trim(),
      pattern: value.pattern.trim(),
      type: value.type,
      enabled: value.enabled,
      cooldownSeconds: value.cooldownSeconds,
      channelScope: value.channelScope,
      discordServerId: value.discordServerId,
      responses: value.responses.map((r) => r.trim()).filter(Boolean),
      channelIds: value.channelIdsText
        .split(',')
        .map((id) => id.trim())
        .filter(Boolean),
    };

    this.saving.set(true);
    this.error.set(null);

    const request = this.editId
      ? this.api.updateTrigger(this.editId, payload)
      : this.api.createTrigger(payload);

    request.subscribe({
      next: () => this.router.navigateByUrl('/triggers'),
      error: () => {
        this.error.set('Enregistrement impossible. Vérifie les champs.');
        this.saving.set(false);
      },
    });
  }
}
