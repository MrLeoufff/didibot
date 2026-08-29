export const GLOBAL_GUILD_ID = '0';

export function isGlobalGuild(guildId: string | null | undefined): boolean {
  return guildId === GLOBAL_GUILD_ID;
}

export function serverDisplayName(name: string, guildId: string): string {
  return isGlobalGuild(guildId) ? 'Global — tous les serveurs' : name;
}

export function serverOptionLabel(name: string, guildId: string): string {
  return isGlobalGuild(guildId)
    ? `${name} — Global (tous les serveurs)`
    : `${name} (${guildId})`;
}
