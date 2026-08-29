import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  readonly error = signal<string | null>(null);
  readonly loading = signal(false);

  readonly form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    const { username, password } = this.form.getRawValue();
    this.auth.login(username, password).subscribe({
      next: () => this.router.navigateByUrl('/dashboard'),
      error: (err) => {
        const status = err?.status;
        const message = err?.error?.detail || err?.error?.message;
        if (typeof message === 'string' && message.trim()) {
          this.error.set(message);
        } else if (status === 401 || status === 400) {
          this.error.set('Identifiants invalides');
        } else if (!status) {
          this.error.set('Impossible de joindre l’API. Réessaie dans un instant.');
        } else {
          this.error.set(`Erreur de connexion (${status})`);
        }
        this.loading.set(false);
      },
    });
  }
}
