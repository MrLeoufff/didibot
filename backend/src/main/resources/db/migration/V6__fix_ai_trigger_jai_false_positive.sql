-- Évite que "j'ai" / "l'ai" déclenchent le trigger IA via \bAI\b
UPDATE trigger_rule
SET pattern = '\bIA\b|(?<![''’])\bAI\b|\bChatGPT\b|\bLLM\b|\bOpenAI\b|\bClaude\b',
    updated_at = NOW()
WHERE name = '🤖 Troll IA'
  AND type = 'REGEX';
