-- Réponses rares + trolls personnalités publiques + pépites Java

ALTER TABLE trigger_response
    ADD COLUMN IF NOT EXISTS rarity VARCHAR(16) NOT NULL DEFAULT 'NORMAL';

ALTER TABLE trigger_response
    DROP CONSTRAINT IF EXISTS chk_trigger_response_rarity;

ALTER TABLE trigger_response
    ADD CONSTRAINT chk_trigger_response_rarity CHECK (rarity IN ('NORMAL', 'RARE'));

CREATE INDEX IF NOT EXISTS idx_trigger_response_rarity ON trigger_response(rarity);

-- Pépites Java supplémentaires
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('Java compile plus vite que ton café refroidit.'),
    ('Le Garbage Collector fait le ménage pendant que tu dors.'),
    ('Spring Boot détecté. Trois annotations et c''est réglé.'),
    ('Le Lead Tech approuve ce message.'),
    ('public static void main est satisfait.')
) AS v(content)
WHERE t.name = '☕ Troll Java'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- Enrichir Linux / Tux
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('Le pingouin approuve.'),
    ('sudo est votre meilleur ami.'),
    ('Arch Linux va bientôt arriver dans la conversation.'),
    ('Ça marche sur Linux.'),
    ('Une nouvelle distribution vient d''être créée.')
) AS v(content)
WHERE t.name = '🐧 Troll Linux'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

DO $$
DECLARE
    server_rec RECORD;
    new_trigger_id BIGINT;
BEGIN
    FOR server_rec IN SELECT id FROM discord_server LOOP

        -- Pool global d'événements rares (jamais matché directement)
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '✨ Événements rares' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (
                name, pattern, type, enabled, cooldown_seconds, channel_scope,
                discord_server_id, status
            )
            VALUES (
                '✨ Événements rares', '___RARE_EVENTS_POOL___', 'EXACT', FALSE, 0, 'ALL',
                server_rec.id, 'APPROVED'
            )
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('🚨 Événement légendaire ! Elon Musk et Linus Torvalds viennent de réagir en même temps. Les probabilités étaient de 1 sur 10 000.', TRUE, new_trigger_id, 'RARE'),
                ('💀 DidiBot a décidé que ce message méritait un troll premium. Profitez-en, c''est rare.', TRUE, new_trigger_id, 'RARE'),
                ('🎰 Jackpot troll ! Cette réponse n''apparaît qu''environ 1 % du temps.', TRUE, new_trigger_id, 'RARE'),
                ('🌟 Événement cosmique : l''univers a validé ce message. Temporairement.', TRUE, new_trigger_id, 'RARE'),
                ('🦄 Une réponse mythique vient d''apparaître. Screenshot obligatoire.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 🇺🇸 Donald Trump
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '🇺🇸 Troll Trump' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🇺🇸 Troll Trump', '\bDonald Trump\b|\bTrump\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('🇺🇸 "Fake News !" vient d''être détecté.', TRUE, new_trigger_id, 'NORMAL'),
                ('Donald Trump vient probablement de revendiquer ce message.', TRUE, new_trigger_id, 'NORMAL'),
                ('Ce message est maintenant soumis à un contrôle de véracité.', TRUE, new_trigger_id, 'NORMAL'),
                ('DidiBot refuse de commenter avant un recompte des messages.', TRUE, new_trigger_id, 'NORMAL'),
                ('"The best message. Everyone says so."', TRUE, new_trigger_id, 'NORMAL'),
                ('🚨 Événement rare : ce message vient d''être déclaré "tremendous".', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 🚀 Jeff Bezos
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '🚀 Troll Bezos' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🚀 Troll Bezos', '\bJeff Bezos\b|\bBezos\b|\bAmazon\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('📦 Votre message sera livré demain avant 22h.', TRUE, new_trigger_id, 'NORMAL'),
                ('Amazon Prime détecté. Livraison de troll en cours.', TRUE, new_trigger_id, 'NORMAL'),
                ('Jeff Bezos vient d''ajouter votre message au panier.', TRUE, new_trigger_id, 'NORMAL'),
                ('Le bot espère recevoir la livraison gratuite.', TRUE, new_trigger_id, 'NORMAL'),
                ('Attention, un drone approche.', TRUE, new_trigger_id, 'NORMAL'),
                ('🛸 Événement rare : livraison par drone Blue Origin confirmée.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 🚗 Elon Musk
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '🚗 Troll Elon Musk' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🚗 Troll Elon Musk', '\bElon Musk\b|\bElon\b|\bMusk\b|\bTesla\b|\bSpaceX\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('🚀 Direction Mars.', TRUE, new_trigger_id, 'NORMAL'),
                ('Elon Musk approuve... ou pas.', TRUE, new_trigger_id, 'NORMAL'),
                ('Ce message est maintenant propulsé par SpaceX.', TRUE, new_trigger_id, 'NORMAL'),
                ('Une mise à jour OTA est en cours.', TRUE, new_trigger_id, 'NORMAL'),
                ('DidiBot investit immédiatement dans le Dogecoin.', TRUE, new_trigger_id, 'NORMAL'),
                ('🚨 Événement légendaire ! Elon Musk et Linus Torvalds viennent de réagir en même temps. Les probabilités étaient de 1 sur 10 000.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 🍎 Steve Jobs
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '🍎 Troll Steve Jobs' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🍎 Troll Steve Jobs', '\bSteve Jobs\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('🍏 "One more thing..."', TRUE, new_trigger_id, 'NORMAL'),
                ('Steve Jobs aurait probablement retiré un port USB.', TRUE, new_trigger_id, 'NORMAL'),
                ('Ce message coûte maintenant 999 €.', TRUE, new_trigger_id, 'NORMAL'),
                ('Design avant fonctionnalité.', TRUE, new_trigger_id, 'NORMAL'),
                ('Think Different.', TRUE, new_trigger_id, 'NORMAL'),
                ('✨ Événement rare : ce message vient d''être présenté sur une keynote.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 💻 Bill Gates
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '💻 Troll Bill Gates' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('💻 Troll Bill Gates', '\bBill Gates\b|\bGates\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('Windows Update est prêt.', TRUE, new_trigger_id, 'NORMAL'),
                ('Bill Gates vient d''envoyer un correctif.', TRUE, new_trigger_id, 'NORMAL'),
                ('Merci de redémarrer pour appliquer ce message.', TRUE, new_trigger_id, 'NORMAL'),
                ('Ce troll nécessite .NET Framework 12.', TRUE, new_trigger_id, 'NORMAL'),
                ('CTRL+ALT+SUPPR.', TRUE, new_trigger_id, 'NORMAL'),
                ('💀 Événement rare : BSOD cosmétique appliqué à ce salon.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 🤖 Mark Zuckerberg
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '🤖 Troll Zuckerberg' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🤖 Troll Zuckerberg', '\bZuckerberg\b|\bMeta\b|\bFacebook\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('Votre message sera utilisé pour améliorer la publicité.', TRUE, new_trigger_id, 'NORMAL'),
                ('Meta collecte déjà vos données.', TRUE, new_trigger_id, 'NORMAL'),
                ('Bienvenue dans le métavers.', TRUE, new_trigger_id, 'NORMAL'),
                ('Ce message appartient désormais à Meta.', TRUE, new_trigger_id, 'NORMAL'),
                ('Merci pour votre contribution aux algorithmes.', TRUE, new_trigger_id, 'NORMAL'),
                ('🕶️ Événement rare : vous venez d''entrer dans le métavers premium.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 🎮 Gabe Newell
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '🎮 Troll Gabe Newell' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🎮 Troll Gabe Newell', '\bGabe Newell\b|\bGaben\b|\bSteam\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('Steam Sale détectée. Votre portefeuille est en danger.', TRUE, new_trigger_id, 'NORMAL'),
                ('90 % de réduction... sur des jeux auxquels vous ne jouerez jamais.', TRUE, new_trigger_id, 'NORMAL'),
                ('Gabe Newell vous remercie.', TRUE, new_trigger_id, 'NORMAL'),
                ('Votre bibliothèque contient maintenant 847 jeux.', TRUE, new_trigger_id, 'NORMAL'),
                ('Vous jouez toujours au même.', TRUE, new_trigger_id, 'NORMAL'),
                ('🎮 Événement rare : Half-Life 3 a été annoncé... dans un rêve.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 🍍 Linus Torvalds
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '🍍 Troll Linus Torvalds' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🍍 Troll Linus Torvalds', '\bLinus Torvalds\b|\bTorvalds\b|\bLinus\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('Git détecté. Les conflits arrivent.', TRUE, new_trigger_id, 'NORMAL'),
                ('Linus n''aurait probablement pas accepté cette Pull Request.', TRUE, new_trigger_id, 'NORMAL'),
                ('"Talk is cheap. Show me the code."', TRUE, new_trigger_id, 'NORMAL'),
                ('Le noyau Linux approuve.', TRUE, new_trigger_id, 'NORMAL'),
                ('Rebase avant de parler.', TRUE, new_trigger_id, 'NORMAL'),
                ('🚨 Événement légendaire ! Elon Musk et Linus Torvalds viennent de réagir en même temps. Les probabilités étaient de 1 sur 10 000.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 💾 Richard Stallman
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '💾 Troll Stallman' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('💾 Troll Stallman', '\bRichard Stallman\b|\bStallman\b|\bRMS\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('GNU/Linux, s''il vous plaît.', TRUE, new_trigger_id, 'NORMAL'),
                ('Logiciel propriétaire détecté.', TRUE, new_trigger_id, 'NORMAL'),
                ('Ce message est sous licence GPL.', TRUE, new_trigger_id, 'NORMAL'),
                ('Libre ou rien.', TRUE, new_trigger_id, 'NORMAL'),
                ('RMS observe cette conversation.', TRUE, new_trigger_id, 'NORMAL'),
                ('📜 Événement rare : ce salon est désormais sous licence AGPL.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- 🍍 Chuck Norris
        IF NOT EXISTS (
            SELECT 1 FROM trigger_rule
            WHERE name = '🍍 Troll Chuck Norris' AND discord_server_id = server_rec.id
        ) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🍍 Troll Chuck Norris', '\bChuck Norris\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id, rarity) VALUES
                ('Chuck Norris n''utilise pas Stack Overflow.', TRUE, new_trigger_id, 'NORMAL'),
                ('C''est Stack Overflow qui consulte Chuck Norris.', TRUE, new_trigger_id, 'NORMAL'),
                ('Chuck Norris compile en une seule instruction.', TRUE, new_trigger_id, 'NORMAL'),
                ('La JVM demande la permission à Chuck Norris avant de démarrer.', TRUE, new_trigger_id, 'NORMAL'),
                ('Même les exceptions évitent Chuck Norris.', TRUE, new_trigger_id, 'NORMAL'),
                ('💀 Événement rare : Chuck Norris a roundhousé ce salon.', TRUE, new_trigger_id, 'RARE');
        END IF;

        -- Rare Java
        INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
        SELECT '☕ Événement rare : la JVM a applaudi ce message.', TRUE, t.id, 'RARE'
        FROM trigger_rule t
        WHERE t.name = '☕ Troll Java'
          AND t.discord_server_id = server_rec.id
          AND NOT EXISTS (
              SELECT 1 FROM trigger_response r
              WHERE r.trigger_id = t.id
                AND r.content = '☕ Événement rare : la JVM a applaudi ce message.'
          );

    END LOOP;
END $$;
