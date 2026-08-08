export type TriggerType = 'EXACT' | 'CONTAINS' | 'STARTS_WITH' | 'REGEX';
export type ChannelScope = 'ALL' | 'INCLUDE' | 'EXCLUDE';

export interface TriggerResponse {
  id: number;
  content: string;
  enabled: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface Trigger {
  id: number;
  name: string;
  pattern: string;
  type: TriggerType;
  enabled: boolean;
  cooldownSeconds: number;
  channelScope: ChannelScope;
  discordServerId: number;
  discordGuildId: string;
  discordServerName: string;
  responses: TriggerResponse[];
  channelIds: string[];
  createdAt?: string;
  updatedAt?: string;
}

export interface TriggerRequest {
  name: string;
  pattern: string;
  type: TriggerType;
  enabled: boolean;
  cooldownSeconds: number;
  channelScope: ChannelScope;
  discordServerId?: number | null;
  discordGuildId?: string | null;
  responses: string[];
  channelIds: string[];
}

export interface DiscordServer {
  id: number;
  discordGuildId: string;
  name: string;
  enabled: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface DiscordServerRequest {
  discordGuildId: string;
  name: string;
  enabled: boolean;
}

export interface TriggerExecution {
  id: number;
  triggerId: number | null;
  discordGuildId: string;
  channelId: string;
  channelName: string;
  userId: string;
  username: string;
  matchedPattern: string;
  triggerName: string;
  responseContent: string;
  executedAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface HealthStatus {
  status: string;
  discordConnected: boolean;
}
