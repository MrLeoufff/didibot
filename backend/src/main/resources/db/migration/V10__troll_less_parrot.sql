ALTER TABLE trigger_rule
    ADD COLUMN fire_chance DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    ADD COLUMN action VARCHAR(16) NOT NULL DEFAULT 'REPLY',
    ADD COLUMN reaction_emoji VARCHAR(64),
    ADD COLUMN cooldown_scope VARCHAR(16) NOT NULL DEFAULT 'SERVER';

ALTER TABLE trigger_rule DROP CONSTRAINT chk_trigger_type;
ALTER TABLE trigger_rule ADD CONSTRAINT chk_trigger_type
    CHECK (type IN ('EXACT', 'CONTAINS', 'STARTS_WITH', 'REGEX', 'GIF'));
ALTER TABLE trigger_rule ADD CONSTRAINT chk_fire_chance
    CHECK (fire_chance >= 0 AND fire_chance <= 1);
ALTER TABLE trigger_rule ADD CONSTRAINT chk_action
    CHECK (action IN ('REPLY', 'REACT', 'BOTH'));
ALTER TABLE trigger_rule ADD CONSTRAINT chk_cooldown_scope
    CHECK (cooldown_scope IN ('SERVER', 'USER'));

CREATE TABLE trigger_cooldown (
    id            BIGSERIAL PRIMARY KEY,
    trigger_id    BIGINT NOT NULL REFERENCES trigger_rule(id) ON DELETE CASCADE,
    guild_id      VARCHAR(32) NOT NULL,
    user_key      VARCHAR(32) NOT NULL DEFAULT '',
    last_fired_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_trigger_cooldown UNIQUE (trigger_id, guild_id, user_key)
);

CREATE TABLE bot_setting (
    key        VARCHAR(64) PRIMARY KEY,
    value      TEXT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE discord_server
    ADD COLUMN welcome_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN welcome_channel_id VARCHAR(32),
    ADD COLUMN welcome_message TEXT;

INSERT INTO trigger_rule (
    name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id,
    fire_chance, action, cooldown_scope
)
SELECT
    'Alerte GIF',
    '___GIF_ALERT___',
    'GIF',
    TRUE,
    45,
    'ALL',
    s.id,
    1.0,
    'REPLY',
    'SERVER'
FROM discord_server s
WHERE s.discord_guild_id = '0'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_rule t
      WHERE t.type = 'GIF' AND t.pattern = '___GIF_ALERT___'
  );

INSERT INTO trigger_response (content, enabled, trigger_id)
SELECT v.content, TRUE, t.id
FROM trigger_rule t
CROSS JOIN (
    VALUES
        ('🚨 Alerte boomer : un GIF vient d''atterrir.'),
        ('Boomer alert. Quelqu''un a sorti le GIF.'),
        ('GIF détecté. Les anciens sont en danger.'),
        ('Tenor a encore frappé. Alerte au boomer.'),
        ('Un GIF ? En 2026 ? Courage.'),
        ('Alerte millennial/boomer : format GIF activé.'),
        ('Ce GIF a probablement 12 ans. Comme le meme.'),
        ('DidiBot (team Java) valide le GIF... à contrecœur.'),
        ('🚨 GIF incoming. Rangez vos PowerPoint.')
) AS v(content)
WHERE t.pattern = '___GIF_ALERT___'
  AND t.type = 'GIF'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r WHERE r.trigger_id = t.id
  );
