import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly api = inject(ApiService);

  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly loading = signal(false);

  readonly form = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { username, password, confirmPassword } = this.form.getRawValue();
    if (password !== confirmPassword) {
      this.error.set('Les mots de passe ne correspondent pas');
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.success.set(null);

    this.api.register({ username: username.trim(), password }).subscribe({
      next: () => {
        this.success.set(
          'Demande envoyée. Tu pourras te connecter seulement après acceptation par un admin.'
        );
        this.form.reset({ username: '', password: '', confirmPassword: '' });
        this.loading.set(false);
      },
      error: (err) => {
        const message = err?.error?.detail || err?.error?.message;
        if (typeof message === 'string' && message.trim()) {
          this.error.set(message);
        } else if (err?.status === 400) {
          this.error.set('Demande invalide. Vérifie le pseudo et le mot de passe.');
        } else {
          this.error.set('Impossible d’envoyer la demande. Réessaie plus tard.');
        }
        this.loading.set(false);
      },
    });
  }
}
