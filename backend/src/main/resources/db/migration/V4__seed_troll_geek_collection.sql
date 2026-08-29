-- Collection troll geek DidiBot : multi-réponses aléatoires par techno
-- Appliqué à tous les serveurs connus (dont le placeholder guild 0)

INSERT INTO discord_server (discord_guild_id, name, enabled)
VALUES ('0', 'Serveur par défaut (à synchroniser)', TRUE)
ON CONFLICT (discord_guild_id) DO NOTHING;

-- Remplace l'ancien seed démo C#
DELETE FROM trigger_rule WHERE name = 'Troll CSharp';

DO $$
DECLARE
    server_rec RECORD;
    new_trigger_id BIGINT;
BEGIN
    FOR server_rec IN SELECT id FROM discord_server LOOP
        -- 🟨 Troll JavaScript
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🟨 Troll JavaScript' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🟨 Troll JavaScript', '\bJavaScript\b|\bJS\b(?![A-Za-z])', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('undefined rejoint la conversation.', TRUE, new_trigger_id),
                ('Encore une dépendance npm de 400 Mo.', TRUE, new_trigger_id),
                ('Il existe déjà un framework pour ça.', TRUE, new_trigger_id),
                ('Aujourd''hui React, demain Angular, après-demain Svelte.', TRUE, new_trigger_id),
                ('JavaScript : "Ça marche sur ma machine."', TRUE, new_trigger_id);
        END IF;

        -- 🔵 Troll TypeScript
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🔵 Troll TypeScript' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🔵 Troll TypeScript', '\bTypeScript\b|\bTS\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('JavaScript avec des garde-fous.', TRUE, new_trigger_id),
                ('Quelqu''un a enfin découvert les types.', TRUE, new_trigger_id),
                ('TypeScript est arrivé pour réparer JavaScript.', TRUE, new_trigger_id),
                ('Compilation en cours...', TRUE, new_trigger_id),
                ('Les erreurs arrivent avant la production. Quelle idée !', TRUE, new_trigger_id);
        END IF;

        -- ☕ Troll Java
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '☕ Troll Java' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('☕ Troll Java', '\bJava\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('☕ Java compile pendant que les autres croisent les doigts.', TRUE, new_trigger_id),
                ('La JVM vous observe.', TRUE, new_trigger_id),
                ('Chez nous, les NullPointerException sont une tradition.', TRUE, new_trigger_id),
                ('"Write Once, Debug Everywhere"... enfin presque.', TRUE, new_trigger_id),
                ('Java détecté. Classe abstraite de troll en cours d''instanciation.', TRUE, new_trigger_id);
        END IF;

        -- 🔷 Troll C#
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🔷 Troll C#' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🔷 Troll C#', 'C#', 'CONTAINS', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('🚨 C# détecté. Java demande officiellement un droit de réponse.', TRUE, new_trigger_id),
                ('Microsoft vient de récupérer votre âme.', TRUE, new_trigger_id),
                ('C# ? Java avec un abonnement Microsoft.', TRUE, new_trigger_id),
                ('Encore un développeur Visual Studio.', TRUE, new_trigger_id),
                ('Attention, une DLL sauvage apparaît.', TRUE, new_trigger_id),
                ('Vous avez prononcé C#. Java prépare sa contre-attaque.', TRUE, new_trigger_id);
        END IF;

        -- 🐘 Troll PHP
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐘 Troll PHP' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐘 Troll PHP', '\bPHP\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('PHP détecté. Le serveur transpire déjà.', TRUE, new_trigger_id),
                ('Le meilleur langage... en 2008.', TRUE, new_trigger_id),
                ('Ça fonctionne, ne touche plus à rien.', TRUE, new_trigger_id),
                ('Laravel sauve encore une fois la réputation de PHP.', TRUE, new_trigger_id),
                ('Le développeur PHP va maintenant tout mettre dans un Controller.', TRUE, new_trigger_id);
        END IF;

        -- 🐍 Troll Python
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐍 Troll Python' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐍 Troll Python', '\bPython\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Encore une IA en préparation.', TRUE, new_trigger_id),
                ('Il y a sûrement une librairie pour ça.', TRUE, new_trigger_id),
                ('Python : 3 lignes de code, 250 dépendances.', TRUE, new_trigger_id),
                ('Attention, indentation obligatoire.', TRUE, new_trigger_id),
                ('Quelqu''un vient d''importer tout PyPI.', TRUE, new_trigger_id);
        END IF;

        -- 🦀 Troll Rust
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🦀 Troll Rust' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🦀 Troll Rust', '\bRust\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Segmentation fault ? Connais pas.', TRUE, new_trigger_id),
                ('Le compilateur vient de refuser votre existence.', TRUE, new_trigger_id),
                ('Rust protège votre mémoire... et votre moral.', TRUE, new_trigger_id),
                ('Compilation estimée : quelques cafés.', TRUE, new_trigger_id),
                ('Borrow Checker > votre Lead Tech.', TRUE, new_trigger_id);
        END IF;

        -- 🐹 Troll Go
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐹 Troll Go' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐹 Troll Go', '\bGolang\b|\bgoroutine|\bGo module|\blangage Go\b|\ben Go\b|\bavec Go\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Compilation terminée avant que tu aies fini de lire ce message.', TRUE, new_trigger_id),
                ('Go détecté. Simplicité activée.', TRUE, new_trigger_id),
                ('Encore un développeur qui adore les goroutines.', TRUE, new_trigger_id),
                ('Error handling intensifies.', TRUE, new_trigger_id),
                ('Pas d''héritage, pas de problème.', TRUE, new_trigger_id);
        END IF;

        -- 💎 Troll Ruby
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '💎 Troll Ruby' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('💎 Troll Ruby', '\bRuby\b|\bRails\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Rails va tout faire pour vous.', TRUE, new_trigger_id),
                ('Convention over Configuration.', TRUE, new_trigger_id),
                ('Le code est beau... jusqu''à la facture serveur.', TRUE, new_trigger_id),
                ('Ruby détecté. Startup en approche.', TRUE, new_trigger_id),
                ('Encore un bundle install de 10 minutes.', TRUE, new_trigger_id);
        END IF;

        -- 🐧 Troll Linux
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐧 Troll Linux' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐧 Troll Linux', '\bLinux\b|\bUbuntu\b|\bArch\b|\bDebian\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Linux détecté. Quelqu''un va dire "chez moi ça marche".', TRUE, new_trigger_id),
                ('sudo est votre meilleur ami.', TRUE, new_trigger_id),
                ('Un Arch Linux va bientôt vous expliquer pourquoi Ubuntu est nul.', TRUE, new_trigger_id),
                ('Le terminal est la vraie interface graphique.', TRUE, new_trigger_id),
                ('Le pingouin approuve.', TRUE, new_trigger_id);
        END IF;

        -- 🪟 Troll Windows
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🪟 Troll Windows' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🪟 Troll Windows', '\bWindows\b|\bWin10\b|\bWin11\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Avez-vous essayé de redémarrer ?', TRUE, new_trigger_id),
                ('Windows Update démarre dans 5...', TRUE, new_trigger_id),
                ('Le registre est une aventure.', TRUE, new_trigger_id),
                ('CTRL+ALT+SUPPR est une philosophie.', TRUE, new_trigger_id),
                ('Une mise à jour est disponible. Vous n''avez pas le choix.', TRUE, new_trigger_id);
        END IF;

        -- 🍎 Troll macOS
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🍎 Troll macOS' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🍎 Troll macOS', '\bmacOS\b|\bMacOS\b|\bMacBook\b|\bOSX\b|\bOS X\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Ça coûte plus cher, donc c''est mieux.', TRUE, new_trigger_id),
                ('Homebrew résout tous les problèmes.', TRUE, new_trigger_id),
                ('Encore un développeur avec un MacBook à 3000 €.', TRUE, new_trigger_id),
                ('Xcode vient de télécharger 25 Go.', TRUE, new_trigger_id),
                ('"Ça marche sur mon Mac."', TRUE, new_trigger_id);
        END IF;

        -- 🐳 Troll Docker
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐳 Troll Docker' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐳 Troll Docker', '\bDocker\b|\bdocker-compose\b|\bdocker compose\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Encore un docker compose up.', TRUE, new_trigger_id),
                ('Ça marche dans le conteneur.', TRUE, new_trigger_id),
                ('Volume introuvable.', TRUE, new_trigger_id),
                ('Image reconstruite pour une faute de frappe.', TRUE, new_trigger_id),
                ('Docker détecté. Kubernetes arrive dans 3... 2...', TRUE, new_trigger_id);
        END IF;

        -- ☸ Troll Kubernetes
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '☸ Troll Kubernetes' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('☸ Troll Kubernetes', '\bKubernetes\b|\bK8s\b|\bk8s\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Personne ne comprend vraiment Kubernetes.', TRUE, new_trigger_id),
                ('Encore un YAML de 800 lignes.', TRUE, new_trigger_id),
                ('Pod CrashLoopBackOff détecté.', TRUE, new_trigger_id),
                ('Vous vouliez juste lancer une application...', TRUE, new_trigger_id),
                ('Le cluster est vivant... normalement.', TRUE, new_trigger_id);
        END IF;

        -- 🗄️ Troll SQL
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🗄️ Troll SQL' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🗄️ Troll SQL', '\bSQL\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('SELECT * FROM coffee;', TRUE, new_trigger_id),
                ('Un JOIN de plus et la base pleure.', TRUE, new_trigger_id),
                ('WHERE oublié. Toute la table est partie.', TRUE, new_trigger_id),
                ('DELETE sans WHERE détecté. 😱', TRUE, new_trigger_id),
                ('La prod retient son souffle.', TRUE, new_trigger_id);
        END IF;

        -- 🐙 Troll Git
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐙 Troll Git' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐙 Troll Git', '\bGit\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Encore un conflit de merge.', TRUE, new_trigger_id),
                ('git push --force détecté. Les anciens pleurent.', TRUE, new_trigger_id),
                ('Il suffisait de faire un git pull.', TRUE, new_trigger_id),
                ('Detached HEAD activé.', TRUE, new_trigger_id),
                ('Le README est toujours à faire.', TRUE, new_trigger_id);
        END IF;

        -- 🤖 Troll IA
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🤖 Troll IA' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🤖 Troll IA', '\bIA\b|\bAI\b|\bChatGPT\b|\bLLM\b|\bOpenAI\b|\bClaude\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('L''IA prend votre travail... doucement.', TRUE, new_trigger_id),
                ('ChatGPT a probablement déjà écrit ce message.', TRUE, new_trigger_id),
                ('Prompt Engineering +10.', TRUE, new_trigger_id),
                ('LLM détecté. Hallucination en approche.', TRUE, new_trigger_id),
                ('Merci de ne pas demander à l''IA de corriger votre prod directement.', TRUE, new_trigger_id);
        END IF;

        -- 💙 Troll VS Code
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '💙 Troll VS Code' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('💙 Troll VS Code', '\bVS Code\b|\bVSCode\b|\bVisual Studio Code\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('VS Code détecté. Extension marketplace en approche.', TRUE, new_trigger_id),
                ('Encore 47 extensions pour afficher une virgule.', TRUE, new_trigger_id),
                ('Ctrl+P va tout résoudre. Probablement.', TRUE, new_trigger_id),
                ('Settings Sync activé. Votre config est partout... même les bugs.', TRUE, new_trigger_id);
        END IF;

        -- 🧠 Troll IntelliJ
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🧠 Troll IntelliJ' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🧠 Troll IntelliJ', '\bIntelliJ\b|\bIntelliJ IDEA\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('IntelliJ détecté. Votre RAM envoie ses condoléances.', TRUE, new_trigger_id),
                ('JetBrains vient d''indexer tout l''univers.', TRUE, new_trigger_id),
                ('Alt+Enter a déjà corrigé votre carrière.', TRUE, new_trigger_id),
                ('IntelliJ : parce que Eclipse existait.', TRUE, new_trigger_id);
        END IF;

        -- 🌑 Troll Eclipse
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🌑 Troll Eclipse' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🌑 Troll Eclipse', '\bEclipse\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Eclipse détecté. Quelqu''un vit encore en 2012.', TRUE, new_trigger_id),
                ('Workspace corrupt. Comme d''habitude.', TRUE, new_trigger_id),
                ('Plugin hell intensifies.', TRUE, new_trigger_id),
                ('Eclipse : le seul IDE qui a besoin d''un IDE pour démarrer.', TRUE, new_trigger_id);
        END IF;

        -- ⌨️ Troll Vim
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '⌨️ Troll Vim' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('⌨️ Troll Vim', '\bVim\b|\bNeovim\b|\bnvim\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Vim détecté. Bonne chance pour quitter.', TRUE, new_trigger_id),
                (':q! est une exit strategy valide.', TRUE, new_trigger_id),
                ('Modes. Toujours des modes.', TRUE, new_trigger_id),
                ('Un utilisateur Vim va bientôt flex sur 3 frappes.', TRUE, new_trigger_id);
        END IF;

        -- 📜 Troll Emacs
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '📜 Troll Emacs' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('📜 Troll Emacs', '\bEmacs\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Emacs détecté. Ou plutôt un OS avec un éditeur inclus.', TRUE, new_trigger_id),
                ('Ctrl+Meta+Alt+Shift+quelque chose.', TRUE, new_trigger_id),
                ('Emacs : installé en 2009, configuré depuis.', TRUE, new_trigger_id),
                ('Votre .emacs fait 4000 lignes. Respect.', TRUE, new_trigger_id);
        END IF;

        -- ⚛️ Troll React
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '⚛️ Troll React' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('⚛️ Troll React', '\bReact\b|\bReactJS\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('React détecté. useEffect va encore tout casser.', TRUE, new_trigger_id),
                ('Encore un state pour un bouton.', TRUE, new_trigger_id),
                ('Virtual DOM > problèmes réels.', TRUE, new_trigger_id),
                ('hooks hooks hooks hooks...', TRUE, new_trigger_id);
        END IF;

        -- 🅰️ Troll Angular
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🅰️ Troll Angular' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🅰️ Troll Angular', '\bAngular\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Angular détecté. Zone.js observe tout.', TRUE, new_trigger_id),
                ('Encore un decorator @Injectable.', TRUE, new_trigger_id),
                ('RxJS : subscribe ou disparaître.', TRUE, new_trigger_id),
                ('Angular : enterprise vibes only.', TRUE, new_trigger_id);
        END IF;

        -- 💚 Troll Vue
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '💚 Troll Vue' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('💚 Troll Vue', '\bVue\.?js\b|\bVuex\b|\bNuxt\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Vue détecté. Progressive framework, progressive excuses.', TRUE, new_trigger_id),
                ('Composition API ou Options API ? Guerre civile.', TRUE, new_trigger_id),
                ('Nuxt va tout générer. Même la doc manquante.', TRUE, new_trigger_id),
                ('Vue : le framework que tout le monde aime... en silence.', TRUE, new_trigger_id);
        END IF;

        -- 🎻 Troll Symfony
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🎻 Troll Symfony' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🎻 Troll Symfony', '\bSymfony\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Symfony détecté. Bundle overload.', TRUE, new_trigger_id),
                ('Encore un service.yaml de 200 lignes.', TRUE, new_trigger_id),
                ('cache:clear. Toujours cache:clear.', TRUE, new_trigger_id),
                ('Symfony : l''enterprise PHP version chic.', TRUE, new_trigger_id);
        END IF;

        -- 🔺 Troll Laravel
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🔺 Troll Laravel' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🔺 Troll Laravel', '\bLaravel\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Laravel détecté. Artisan va tout faire.', TRUE, new_trigger_id),
                ('Eloquent : magie jusqu''au N+1.', TRUE, new_trigger_id),
                ('php artisan make:excuse', TRUE, new_trigger_id),
                ('Laravel sauve PHP. Encore.', TRUE, new_trigger_id);
        END IF;

        -- 🌱 Troll Spring Boot
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🌱 Troll Spring Boot' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🌱 Troll Spring Boot', '\bSpring Boot\b|\bSpringBoot\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Spring Boot détecté. Autoconfiguration intensifies.', TRUE, new_trigger_id),
                ('Encore une annotation @Autowired.', TRUE, new_trigger_id),
                ('Le contexte démarre... un café plus tard.', TRUE, new_trigger_id),
                ('Spring Boot : 200 dépendances pour Hello World.', TRUE, new_trigger_id);
        END IF;

        -- 🟩 Troll Node.js
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🟩 Troll Node.js' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🟩 Troll Node.js', '\bNode\.?js\b|\bNodeJS\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Node.js détecté. callback hell nostalgia.', TRUE, new_trigger_id),
                ('node_modules pèse plus lourd que le projet.', TRUE, new_trigger_id),
                ('require(''chaos'')', TRUE, new_trigger_id),
                ('Event loop : tourne en rond, comme la réunion.', TRUE, new_trigger_id);
        END IF;

        -- 🔴 Troll Oracle
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🔴 Troll Oracle' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🔴 Troll Oracle', '\bOracle\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Oracle détecté. Sortez la carte bleue.', TRUE, new_trigger_id),
                ('Licence check in progress...', TRUE, new_trigger_id),
                ('ORA-00600 : erreur interne, comme le budget.', TRUE, new_trigger_id),
                ('Oracle : la base qui facture même vos SELECT.', TRUE, new_trigger_id);
        END IF;

        -- 🐬 Troll MySQL
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐬 Troll MySQL' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐬 Troll MySQL', '\bMySQL\b|\bMariaDB\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('MySQL détecté. CHARSET = confusion.', TRUE, new_trigger_id),
                ('InnoDB ou MyISAM ? Débat éternel.', TRUE, new_trigger_id),
                ('Encore un index oublié.', TRUE, new_trigger_id),
                ('MySQL : simple, jusqu''à la prod.', TRUE, new_trigger_id);
        END IF;

        -- 🐘 Troll PostgreSQL
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐘 Troll PostgreSQL' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐘 Troll PostgreSQL', '\bPostgreSQL\b|\bPostgres\b|\bpsql\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('PostgreSQL détecté. JSONB + fierté.', TRUE, new_trigger_id),
                ('Le vrai SQL, version open source.', TRUE, new_trigger_id),
                ('EXPLAIN ANALYZE va vous humilier.', TRUE, new_trigger_id),
                ('Postgres : parce que MySQL c''était trop mainstream.', TRUE, new_trigger_id);
        END IF;

        -- ☁️ Troll AWS
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '☁️ Troll AWS' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('☁️ Troll AWS', '\bAWS\b|\bAmazon Web Services\b|\bEC2\b|\bLambda\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('AWS détecté. La facture arrive demain.', TRUE, new_trigger_id),
                ('Encore un rôle IAM mal configuré.', TRUE, new_trigger_id),
                ('us-east-1 est down. Comme toujours.', TRUE, new_trigger_id),
                ('Cloud : louer l''ordinateur de quelqu''un d''autre.', TRUE, new_trigger_id);
        END IF;

        -- 🔷 Troll Azure
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🔷 Troll Azure' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🔷 Troll Azure', '\bAzure\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Azure détecté. Portal loading...', TRUE, new_trigger_id),
                ('Microsoft Cloud : Active Directory inclus, bonheur non.', TRUE, new_trigger_id),
                ('Encore une Resource Group abandonnée.', TRUE, new_trigger_id),
                ('Azure : AWS, mais en bleu.', TRUE, new_trigger_id);
        END IF;

        -- 🐙 Troll GitHub
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🐙 Troll GitHub' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🐙 Troll GitHub', '\bGitHub\b|\bGithub\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('GitHub détecté. Actions va brûler vos minutes.', TRUE, new_trigger_id),
                ('Encore une PR sans reviewers.', TRUE, new_trigger_id),
                ('Copilot a déjà commenté à votre place.', TRUE, new_trigger_id),
                ('main is protected. Votre ego aussi.', TRUE, new_trigger_id);
        END IF;

        -- 🦊 Troll GitLab
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🦊 Troll GitLab' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🦊 Troll GitLab', '\bGitLab\b|\bGitlab\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('GitLab détecté. CI/CD YAML intensifies.', TRUE, new_trigger_id),
                ('Self-hosted : parce que vous aimez la souffrance.', TRUE, new_trigger_id),
                ('Pipeline failed. Évidemment.', TRUE, new_trigger_id),
                ('GitLab : tout-en-un, y compris les tickets oubliés.', TRUE, new_trigger_id);
        END IF;

        -- 🪣 Troll Bitbucket
        IF NOT EXISTS (SELECT 1 FROM trigger_rule WHERE name = '🪣 Troll Bitbucket' AND discord_server_id = server_rec.id) THEN
            INSERT INTO trigger_rule (name, pattern, type, enabled, cooldown_seconds, channel_scope, discord_server_id, status)
            VALUES ('🪣 Troll Bitbucket', '\bBitbucket\b', 'REGEX', TRUE, 45, 'ALL', server_rec.id, 'APPROVED')
            RETURNING id INTO new_trigger_id;

            INSERT INTO trigger_response (content, enabled, trigger_id) VALUES
                ('Bitbucket détecté. Atlassian tax activée.', TRUE, new_trigger_id),
                ('Quelqu''un utilise encore Bitbucket en 2026 ?', TRUE, new_trigger_id),
                ('Pipelines : la CI dont personne ne parle.', TRUE, new_trigger_id),
                ('Bitbucket : GitHub pour ceux qui ont déjà Jira.', TRUE, new_trigger_id);
        END IF;

    END LOOP;
END $$;
