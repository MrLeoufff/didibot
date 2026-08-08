CREATE TABLE discord_server (
    id              BIGSERIAL PRIMARY KEY,
    discord_guild_id VARCHAR(32) NOT NULL UNIQUE,
    name            VARCHAR(255) NOT NULL,
    enabled         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE trigger_rule (
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    pattern            VARCHAR(512) NOT NULL,
    type               VARCHAR(32) NOT NULL,
    enabled            BOOLEAN NOT NULL DEFAULT TRUE,
    cooldown_seconds   INTEGER NOT NULL DEFAULT 30,
    channel_scope      VARCHAR(32) NOT NULL DEFAULT 'ALL',
    discord_server_id  BIGINT NOT NULL REFERENCES discord_server(id) ON DELETE CASCADE,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_trigger_type CHECK (type IN ('EXACT', 'CONTAINS', 'STARTS_WITH', 'REGEX')),
    CONSTRAINT chk_channel_scope CHECK (channel_scope IN ('ALL', 'INCLUDE', 'EXCLUDE')),
    CONSTRAINT chk_cooldown CHECK (cooldown_seconds >= 0)
);

CREATE INDEX idx_trigger_rule_server ON trigger_rule(discord_server_id);
CREATE INDEX idx_trigger_rule_enabled ON trigger_rule(enabled);

CREATE TABLE trigger_response (
    id          BIGSERIAL PRIMARY KEY,
    content     TEXT NOT NULL,
    enabled     BOOLEAN NOT NULL DEFAULT TRUE,
    trigger_id  BIGINT NOT NULL REFERENCES trigger_rule(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trigger_response_trigger ON trigger_response(trigger_id);

CREATE TABLE trigger_channel (
    id               BIGSERIAL PRIMARY KEY,
    discord_channel_id VARCHAR(32) NOT NULL,
    trigger_id       BIGINT NOT NULL REFERENCES trigger_rule(id) ON DELETE CASCADE,
    CONSTRAINT uq_trigger_channel UNIQUE (trigger_id, discord_channel_id)
);

CREATE TABLE trigger_execution (
    id                 BIGSERIAL PRIMARY KEY,
    trigger_id         BIGINT REFERENCES trigger_rule(id) ON DELETE SET NULL,
    discord_server_id  BIGINT REFERENCES discord_server(id) ON DELETE SET NULL,
    discord_guild_id   VARCHAR(32) NOT NULL,
    channel_id         VARCHAR(32) NOT NULL,
    channel_name       VARCHAR(255),
    user_id            VARCHAR(32) NOT NULL,
    username           VARCHAR(255) NOT NULL,
    matched_pattern    VARCHAR(512) NOT NULL,
    trigger_name       VARCHAR(255) NOT NULL,
    response_content   TEXT,
    executed_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trigger_execution_executed_at ON trigger_execution(executed_at DESC);
CREATE INDEX idx_trigger_execution_guild ON trigger_execution(discord_guild_id);
