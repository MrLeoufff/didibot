import { TriggerType } from '../models/api.models';

export function matchesPattern(
  type: TriggerType,
  pattern: string,
  message: string
): boolean | 'invalid' {
  if (!pattern || !message.trim()) {
    return false;
  }
  const content = message.trim();
  const needle = pattern.trim();
  switch (type) {
    case 'EXACT':
      return content.toLowerCase() === needle.toLowerCase();
    case 'CONTAINS':
      return content.toLowerCase().includes(needle.toLowerCase());
    case 'STARTS_WITH':
      return content.toLowerCase().startsWith(needle.toLowerCase());
    case 'REGEX':
      try {
        return new RegExp(needle, 'iu').test(content);
      } catch {
        return 'invalid';
      }
    case 'GIF':
      return false;
  }
}
