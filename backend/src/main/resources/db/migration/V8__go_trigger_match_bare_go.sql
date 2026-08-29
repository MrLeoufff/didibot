-- Accepte "go", "Go", "GO", "gO", etc. (comme JS / JavaScript)
UPDATE trigger_rule
SET pattern = '\bGo\b|\bGolang\b|\bgoroutine\b|\bGo module\b|\blangage Go\b',
    updated_at = NOW()
WHERE name = '🐹 Troll Go'
  AND type = 'REGEX';
