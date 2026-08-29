ALTER TABLE trigger_rule
    ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT 'APPROVED',
    ADD COLUMN proposed_by VARCHAR(255),
    ADD COLUMN proposed_by_discord_id VARCHAR(32),
    ADD COLUMN reviewed_at TIMESTAMPTZ;

ALTER TABLE trigger_rule
    ADD CONSTRAINT chk_trigger_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED'));

CREATE INDEX idx_trigger_rule_status ON trigger_rule(status);

UPDATE trigger_rule SET status = 'APPROVED' WHERE status IS NULL OR status = '';
