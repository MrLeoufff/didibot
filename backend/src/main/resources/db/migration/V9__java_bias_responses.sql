-- DidiBot défend clairement Java dans ses trolls

-- ☕ Java : fierté assumée
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('☕ Java détecté. Bon goût confirmé.'),
    ('La JVM approuve ce message. Les autres langages prennent des notes.'),
    ('Java : mature, portable, et toujours debout en prod.'),
    ('Spring Boot + Java = la combo des gens sérieux.'),
    ('DidiBot est team Java. Point final.'),
    ('NullPointerException ? Au moins c''est honnête. Pas comme certains runtimes...'),
    ('Write once, run anywhere. Marketing ? Non : vécu.')
) AS v(content)
WHERE t.name = '☕ Troll Java'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- 🔷 C# : troll pro-Java systématique
DELETE FROM trigger_response
WHERE trigger_id IN (SELECT id FROM trigger_rule WHERE name = '🔷 Troll C#');

INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, v.rarity::varchar
FROM trigger_rule t
CROSS JOIN (VALUES
    ('🚨 C# détecté. Java demande officiellement un droit de réponse. ☕', 'NORMAL'),
    ('C# ? Java avec un abonnement Microsoft.', 'NORMAL'),
    ('Vous avez prononcé C#. Java prépare sa contre-attaque.', 'NORMAL'),
    ('DidiBot est team JAVA > C#. Désolé pour ta DLL.', 'NORMAL'),
    ('Microsoft a copié. Java a inventé. On se comprend.', 'NORMAL'),
    ('Visual Studio clignote. La JVM, elle, tient la prod.', 'NORMAL'),
    ('Encore un qui a choisi C#... Java t''attend les bras ouverts (et typés).', 'NORMAL'),
    ('☕ Événement rare : conversion C# → Java en cours. Résistance inutile.', 'RARE')
) AS v(content, rarity)
WHERE t.name = '🔷 Troll C#';

-- 🟨 JavaScript
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('JS détecté. Java regarde ça depuis sa JVM stable.'),
    ('undefined ? En Java, le compilateur t''aurait arrêté bien avant.'),
    ('DidiBot rappelle gentiment que Java n''a pas besoin de 47 bundlers.')
) AS v(content)
WHERE t.name = '🟨 Troll JavaScript'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- 🔵 TypeScript
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('TypeScript : Java pour ceux qui ont peur de quitter JavaScript.'),
    ('Des types ? Bienvenue. Java les avait avant que ce soit cool.')
) AS v(content)
WHERE t.name = '🔵 Troll TypeScript'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- 🐘 PHP
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('PHP détecté. Java prépare un plan de migration... humanitaire.'),
    ('DidiBot (team Java) refuse de commenter ce choix de carrière.')
) AS v(content)
WHERE t.name = '🐘 Troll PHP'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- 🐍 Python
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('Python détecté. Sympa pour les scripts. Pour la prod ? Java appelle.'),
    ('Indentation cute. Typage fort cute aussi — demande à Java.')
) AS v(content)
WHERE t.name = '🐍 Troll Python'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- 🦀 Rust
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('Rust compile longtemps. Java, lui, a déjà démarré le service.'),
    ('Borrow checker sympa. Garbage Collector aussi — team Java.')
) AS v(content)
WHERE t.name = '🦀 Troll Rust'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- 🐹 Go
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('Go détecté. Simple, oui. Écosystème enterprise ? Java sourit.'),
    ('Pas d''héritage en Go. En Java on a le choix — et Spring.')
) AS v(content)
WHERE t.name = '🐹 Troll Go'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- 💎 Ruby
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'NORMAL'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('Ruby détecté. Java propose une reconversion en douceur.')
) AS v(content)
WHERE t.name = '💎 Troll Ruby'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );

-- Rare events globaux : biais Java
INSERT INTO trigger_response (content, enabled, trigger_id, rarity)
SELECT v.content, TRUE, t.id, 'RARE'
FROM trigger_rule t
CROSS JOIN (VALUES
    ('☕ Événement légendaire : Java vient de gagner le débat. Comme d''habitude.'),
    ('🏆 DidiBot déclare officiellement : JAVA > C#. Screenshot pour l''histoire.')
) AS v(content)
WHERE t.name = '✨ Événements rares'
  AND NOT EXISTS (
      SELECT 1 FROM trigger_response r
      WHERE r.trigger_id = t.id AND r.content = v.content
  );
