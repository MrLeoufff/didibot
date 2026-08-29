import { Component, OnInit, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BotSettings, WelcomeSettings } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';
import { apiErrorMessage } from '../../core/utils/http-error';
import { serverDisplayName } from '../../core/utils/server-label';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss',
})
export class SettingsComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly ok = signal<string | null>(null);
  readonly serverLabel = serverDisplayName;

  readonly form = this.fb.nonNullable.group({
    avatarPercent: [12, [Validators.required, Validators.min(0), Validators.max(100)]],
    rarePercent: [1, [Validators.required, Validators.min(0), Validators.max(100)]],
    adminChannelId: [''],
    servers: this.fb.array<FormGroup>([]),
  });

  get servers(): FormArray<FormGroup> {
    return this.form.controls.servers;
  }

  ngOnInit(): void {
    this.api.getSettings().subscribe({
      next: (settings) => {
        this.patch(settings);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(apiErrorMessage(err, 'Impossible de charger les réglages.'));
        this.loading.set(false);
      },
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const payload: BotSettings = {
      avatarImageChance: Number(value.avatarPercent) / 100,
      rareEventChance: Number(value.rarePercent) / 100,
      adminChannelId: value.adminChannelId.trim(),
      servers: this.servers.controls.map((row) => {
        const v = row.getRawValue() as WelcomeSettings & { welcomeChannelId: string; welcomeMessage: string };
        return {
          serverId: v.serverId,
          name: v.name,
          discordGuildId: v.discordGuildId,
          welcomeEnabled: v.welcomeEnabled,
          welcomeChannelId: (v.welcomeChannelId ?? '').toString().trim() || null,
          welcomeMessage: (v.welcomeMessage ?? '').toString().trim() || null,
        };
      }),
    };
    this.saving.set(true);
    this.error.set(null);
    this.ok.set(null);
    this.api.saveSettings(payload).subscribe({
      next: (saved) => {
        this.patch(saved);
        this.saving.set(false);
        this.ok.set('Réglages en mémoire. Pas besoin de toucher au .env.');
      },
      error: (err) => {
        this.saving.set(false);
        this.error.set(apiErrorMessage(err, 'Enregistrement impossible.'));
      },
    });
  }

  private patch(settings: BotSettings): void {
    this.servers.clear();
    for (const server of settings.servers ?? []) {
      this.servers.push(this.welcomeGroup(server));
    }
    this.form.patchValue({
      avatarPercent: Math.round((settings.avatarImageChance ?? 0) * 1000) / 10,
      rarePercent: Math.round((settings.rareEventChance ?? 0) * 1000) / 10,
      adminChannelId: settings.adminChannelId ?? '',
    });
  }

  private welcomeGroup(server: WelcomeSettings): FormGroup {
    return this.fb.nonNullable.group({
      serverId: [server.serverId],
      name: [server.name],
      discordGuildId: [server.discordGuildId],
      welcomeEnabled: [server.welcomeEnabled],
      welcomeChannelId: [server.welcomeChannelId ?? ''],
      welcomeMessage: [server.welcomeMessage ?? 'Bienvenue {mention}. DidiBot t\'a à l\'œil.'],
    });
  }
}
