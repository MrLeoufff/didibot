import { TriggerStatus, TriggerType } from './api.models';

export const TRIGGER_TYPE_LABELS: Record<TriggerType, string> = {
  CONTAINS: 'Contient',
  EXACT: 'Exact',
  STARTS_WITH: 'Commence par',
  REGEX: 'Regex',
};

export const TRIGGER_TYPE_HINTS: Record<TriggerType, string> = {
  CONTAINS: 'Réagit si ce texte apparaît n’importe où dans le message (casse ignorée).',
  EXACT: 'Réagit seulement si le message est exactement ce texte.',
  STARTS_WITH: 'Réagit si le message commence par ce texte.',
  REGEX: 'Expression régulière, insensible à la casse. Exemple : \\bgo\\b',
};

export const TRIGGER_STATUS_LABELS: Record<TriggerStatus, string> = {
  PENDING: 'En attente',
  APPROVED: 'Approuvé',
  REJECTED: 'Refusé',
};
