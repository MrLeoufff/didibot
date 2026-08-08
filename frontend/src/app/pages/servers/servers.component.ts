import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DiscordServer } from '../../core/models/api.models';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-servers',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './servers.component.html',
  styleUrl: './servers.component.scss',
})
export class ServersComponent implements OnInit {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);

  readonly servers = signal<DiscordServer[]>([]);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    discordGuildId: ['', Validators.required],
    enabled: true,
  });

  ngOnInit(): void {
    this.reload();
  }

  reload(): void {
    this.loading.set(true);
    this.api.getServers().subscribe({
      next: (servers) => {
        this.servers.set(servers);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Impossible de charger les serveurs.');
        this.loading.set(false);
      },
    });
  }

  create(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.api.createServer(this.form.getRawValue()).subscribe({
      next: () => {
        this.form.reset({ name: '', discordGuildId: '', enabled: true });
        this.reload();
      },
      error: () => this.error.set('Création impossible (guildId déjà utilisé ?).'),
    });
  }

  toggle(server: DiscordServer): void {
    const request = server.enabled
      ? this.api.disableServer(server.id)
      : this.api.enableServer(server.id);
    request.subscribe({
      next: () => this.reload(),
      error: () => this.error.set('Échec du changement d’état.'),
    });
  }
}
