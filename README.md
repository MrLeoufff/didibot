# DidiBot — Bot Discord + Admin Angular

Bot Discord Spring Boot / JDA avec interface d’administration Angular.  
Déploiement Docker Compose, HTTPS via Caddy en production.

## Architecture

```text
Internet
   │
   ▼
Caddy (HTTPS)
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
- Angular 21, RxJS
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

## Production

```bash
docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d
```

Le frontend rejoint le réseau Docker externe `web` (reverse-proxy). Ne pas écraser `.env` ni les fichiers Flyway `*.sql` déjà appliqués (checksums : rester en **LF**, voir `.gitattributes`).

### Sauvegarde PostgreSQL

```bash
chmod +x scripts/backup-postgres.sh
./scripts/backup-postgres.sh
```

Le dump gzip va dans `backups/` (hors git). Les 14 plus récents sont conservés.

### Angular

Garder le frontend en **Angular 21**. Dependabot ignore les majeures (pas de saut vers 22 sans décision explicite).

## Auth admin & propositions

Les identifiants se configurent dans `.env` (voir `.env.example`).

- Login : `/login` (compte env admin ou compte approuvé)
- Demande de compte : `/register` (reste en `PENDING` jusqu’à acceptation)
- Admin : page `/users` pour accepter / refuser les comptes
- Proposition publique : `/propose`
- Discord : `/help` `/ping` `/triggers` `/stats` `/propose-trigger`
- Les propositions restent en `PENDING` jusqu’à approbation admin
- Événements rares : ~1 % des réponses (`DISCORD_RARE_EVENT_CHANCE`, défaut `0.01`)
- Serveur **Global** (guild `0`) : la règle s’applique partout, sauf si le même motif existe déjà en local

## Discord

1. Créer une application bot
2. Activer **Message Content Intent**
3. Copier le **Bot Token** dans `.env`
4. Inviter le bot (permissions Send Messages + Read Message History)
5. Ne pas lancer deux instances avec le même token (`DISCORD_ENABLED=false` en local si la prod tourne)
6. Optionnel : `DISCORD_ADMIN_CHANNEL_ID` pour notifier les nouvelles propositions dans un salon

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
