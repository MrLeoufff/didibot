-- Serveur placeholder : sera remplacé / complété automatiquement à la connexion du bot
INSERT INTO discord_server (discord_guild_id, name, enabled)
VALUES ('0', 'Serveur par défaut (à synchroniser)', TRUE);

INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id)
VALUES (
    'Troll CSharp',
    'C#',
    'CONTAINS',
    TRUE,
    30,
    'ALL',
    (SELECT id FROM discord_server WHERE discord_guild_id = '0')
);

INSERT INTO trigger_response (content, enabled, trigger_id)
VALUES
    ('🚨 C# détecté. Java demande officiellement un droit de réponse. ☕', TRUE,
     (SELECT id FROM trigger_rule WHERE name = 'Troll CSharp')),
    ('C# ? Java avec une cravate Microsoft.', TRUE,
     (SELECT id FROM trigger_rule WHERE name = 'Troll CSharp')),
    ('.NET vient d''entrer dans la conversation.', TRUE,
     (SELECT id FROM trigger_rule WHERE name = 'Troll CSharp')),
    ('Une pensée pour la JVM qui doit regarder ça.', TRUE,
     (SELECT id FROM trigger_rule WHERE name = 'Troll CSharp'));
