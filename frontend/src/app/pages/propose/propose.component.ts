import { Component, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { TriggerType } from '../../core/models/api.models';
import { TRIGGER_TYPE_HINTS, TRIGGER_TYPE_LABELS } from '../../core/models/trigger.labels';
import { ApiService } from '../../core/services/api.service';
import { AuthService } from '../../core/services/auth.service';
import { apiErrorMessage } from '../../core/utils/http-error';

@Component({
  selector: 'app-propose',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './propose.component.html',
  styleUrl: './propose.component.scss',
})
export class ProposeComponent {
  private readonly api = inject(ApiService);
  private readonly fb = inject(FormBuilder);
  readonly auth = inject(AuthService);

  readonly types: TriggerType[] = ['CONTAINS', 'EXACT', 'STARTS_WITH', 'REGEX'];
  readonly typeLabels = TRIGGER_TYPE_LABELS;
  readonly typeHints = TRIGGER_TYPE_HINTS;
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  readonly form = this.fb.nonNullable.group({
    name: ['', Validators.required],
    pattern: ['', Validators.required],
    type: this.fb.nonNullable.control<TriggerType>('CONTAINS'),
    cooldownSeconds: [30, [Validators.required, Validators.min(0)]],
    proposedBy: [''],
    discordGuildId: [''],
    responses: this.fb.nonNullable.array([this.fb.nonNullable.control('', Validators.required)]),
  });

  get responses(): FormArray {
    return this.form.controls.responses;
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

  invalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  responseInvalid(index: number): boolean {
    const control = this.responses.at(index);
    return control.invalid && (control.touched || control.dirty);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error.set('Complète les champs obligatoires (nom, motif, au moins une réponse).');
      return;
    }
    this.saving.set(true);
    this.error.set(null);
    this.success.set(null);

    const value = this.form.getRawValue();
    const responses = value.responses.map((item) => item.trim()).filter(Boolean);
    if (responses.length === 0) {
      this.error.set('Ajoute au moins une réponse.');
      this.saving.set(false);
      return;
    }

    this.api
      .proposeTrigger({
        name: value.name.trim(),
        pattern: value.pattern.trim(),
        type: value.type,
        cooldownSeconds: Number(value.cooldownSeconds) || 0,
        proposedBy: value.proposedBy.trim() || null,
        discordGuildId: value.discordGuildId.trim() || null,
        responses,
      })
      .subscribe({
        next: (created) => {
          this.success.set(
            `Proposition #${created.id} envoyée. Un admin doit l’approuver avant activation.`
          );
          this.form.reset({
            name: '',
            pattern: '',
            type: 'CONTAINS',
            cooldownSeconds: 30,
            proposedBy: '',
            discordGuildId: '',
            responses: [''],
          });
          this.responses.clear();
          this.responses.push(this.fb.nonNullable.control('', Validators.required));
          this.saving.set(false);
        },
        error: (err) => {
          this.error.set(apiErrorMessage(err, 'Envoi impossible. Vérifie les champs.'));
          this.saving.set(false);
        },
      });
  }
}
