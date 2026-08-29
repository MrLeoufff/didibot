# DidiBot — Bot Discord + Admin Angular

Bot Discord Spring Boot / JDA avec interface d’administration Angular.  
Déploiement Docker Compose, edge HTTPS via **Caddy** en production.

## Architecture

```text
Internet
   │
   ▼
Caddy (HTTPS) ── didibot.dwg-dev.fr
   │
   ▼
Nginx (frontend interne)
   ├── /          → Angular
   └── /api       → Spring Boot
                      ├── PostgreSQL
                      └── Discord / JDA
```

## Stack

- Java 21, Spring Boot, JDA, PostgreSQL, Flyway
- Angular 19, RxJS
- Docker Compose
- Caddy (prod) / Nginx interne (front)

## Démarrage local

```bash
cp .env.example .env
# Renseigner DISCORD_TOKEN=...

docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d
```

| Service | URL |
|---------|-----|
| Admin Angular | http://localhost:8088 |
| API | http://localhost:8088/api |
| Swagger | http://localhost:8088/swagger-ui/index.html |
| Health | http://localhost:8088/api/health |

## Production (m710q)

- Chemin serveur : `/opt/didibot`
- URL : https://didibot.dwg-dev.fr

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d
```

### Sauvegarde PostgreSQL

Sur le serveur (`/opt/didibot`) :

```bash
chmod +x scripts/backup-postgres.sh
./scripts/backup-postgres.sh
```

Le dump gzip va dans `backups/` (hors git). Les 14 plus récents sont conservés. À planifier en cron si besoin, par exemple tous les jours à 3 h :

```bash
0 3 * * * /opt/didibot/scripts/backup-postgres.sh
```

Les fichiers Flyway `*.sql` doivent rester en **LF** (voir `.gitattributes`) : un checksum CRLF casse les migrations déjà appliquées.

### Angular

Garder le frontend en **Angular 19**. Dependabot ignore les majeures `@angular/*` pour éviter un nouveau casse-build.

## DNS Hostinger (`dwg-dev.fr`)

Dans **Hostinger → Domaines → dwg-dev.fr → DNS / Zone DNS**, créer :

| Type | Nom | Valeur | TTL |
|------|-----|--------|-----|
| **A** | `didibot` | `90.1.1.33` | 300 (ou Auto) |

Résultat attendu : `didibot.dwg-dev.fr` → `90.1.1.33`

Notes :

- Ce n’est **pas** une “redirection” HTTP Hostinger : c’est un enregistrement **A** (DNS).
- Ne crée pas de “Redirection de domaine” / URL redirect pour ce sous-domaine.
- Après propagation (souvent quelques minutes), Caddy obtient le certificat Let’s Encrypt tout seul.
- Vérification : `nslookup didibot.dwg-dev.fr` doit renvoyer `90.1.1.33`.

### Sous-domaines déjà utilisés (ne pas casser)

| Sous-domaine | Rôle |
|--------------|------|
| `support.dwg-dev.fr` | Zammad |
| `media.dwg-dev.fr` | Média (Raspberry) |

### À retirer / ignorer côté Hostinger

Si des enregistrements existent encore pour **`reneleliard.online`** (domaine abandonné), tu peux les supprimer côté Hostinger / registrar.  
Sur le serveur, ces blocs ont déjà été retirés du Caddyfile.

## Auth admin & propositions

```env
ADMIN_USERNAME=admin
ADMIN_PASSWORD=...
JWT_SECRET=...
```

- Login : `/login` (admin env ou compte approuvé)
- Demande de compte : `/register` (reste en `PENDING` jusqu’à acceptation)
- Admin : page `/users` pour accepter / refuser les comptes
- Proposition publique : `/propose`
- Discord : `/propose-trigger`
- Les propositions restent en `PENDING` jusqu’à approbation admin
- Événements rares : ~1 % des réponses (`DISCORD_RARE_EVENT_CHANCE`, défaut `0.01`)
- Serveur **Global** (guild `0`) : la règle s’applique partout, sauf si le même motif existe déjà en local

## Discord

1. Créer une application bot
2. Activer **Message Content Intent**
3. Copier le **Bot Token** dans `.env`
4. Inviter le bot (permissions Send Messages + Read Message History)
5. Ne pas lancer deux instances avec le même token (`DISCORD_ENABLED=false` en local si la prod tourne)

## Structure

```text
BotDiscord/
├── backend/                 # Spring Boot + JDA
├── frontend/                # Angular admin
├── scripts/backup-postgres.sh
├── docker-compose.yml
├── docker-compose.dev.yml   # ports locaux
├── docker-compose.prod.yml  # réseau Docker `web` (Caddy)
├── .env.example
└── README.md
```
