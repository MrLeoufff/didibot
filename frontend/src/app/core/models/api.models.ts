export type TriggerType = 'EXACT' | 'CONTAINS' | 'STARTS_WITH' | 'REGEX';
export type ChannelScope = 'ALL' | 'INCLUDE' | 'EXCLUDE';
export type TriggerStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export type ResponseRarity = 'NORMAL' | 'RARE';

export interface TriggerResponse {
  id: number;
  content: string;
  enabled: boolean;
  rarity?: ResponseRarity;
  createdAt?: string;
  updatedAt?: string;
}

export interface TriggerResponseInput {
  content: string;
  enabled?: boolean;
  rarity?: ResponseRarity;
}

export interface Trigger {
  id: number;
  name: string;
  pattern: string;
  type: TriggerType;
  enabled: boolean;
  status: TriggerStatus;
  proposedBy?: string | null;
  proposedByDiscordId?: string | null;
  reviewedAt?: string | null;
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
  responses: TriggerResponseInput[];
  channelIds: string[];
}

export interface TriggerProposeRequest {
  name: string;
  pattern: string;
  type: TriggerType;
  cooldownSeconds: number;
  discordGuildId?: string | null;
  proposedBy?: string | null;
  responses: string[];
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

export interface NameCount {
  name: string;
  count: number;
}

export interface BotStats {
  repliesToday: number;
  repliesLast7Days: number;
  repliesAllTime: number;
  activeTriggers: number;
  pendingTriggers: number;
  serverCount: number;
  topTriggers: NameCount[];
  topUsers: NameCount[];
}

export type UserStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface LoginResponse {
  token: string;
  username: string;
  role: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
}

export interface AppUser {
  id: number;
  username: string;
  status: UserStatus;
  requestedAt?: string | null;
  reviewedAt?: string | null;
}
