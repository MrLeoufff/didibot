# Cahier des charges — Bot Discord de réponses automatiques

## 1. Présentation du projet

Le projet consiste à développer un bot Discord capable de détecter certains mots, expressions ou motifs dans les messages d’un serveur et d’y répondre automatiquement.

Exemple :

```text
Utilisateur :
Franchement C# c'est pas mal.

Bot :
🚨 C# détecté.
Java demande officiellement un droit de réponse. ☕
```

L’objectif initial est humoristique, mais l’architecture devra permettre d’étendre le bot à d’autres usages : messages d’accueil, réactions, modération légère, commandes Discord, statistiques et administration depuis une interface web.

Le backend sera développé avec **Java / Spring Boot**.

Une interface d’administration en **Angular** sera développée dans une seconde phase.

---

## 2. Objectifs

La première version doit permettre de :

- connecter un bot à un ou plusieurs serveurs Discord ;
- écouter les messages ;
- détecter des mots-clés ;
- déclencher automatiquement une réponse ;
- associer plusieurs réponses à un même déclencheur ;
- choisir éventuellement une réponse aléatoire ;
- éviter les boucles et le spam ;
- désactiver temporairement une règle ;
- gérer les règles sans modifier le code source.

À terme, tout devra pouvoir être administré depuis Angular.

---

## 3. Stack technique

### Backend

- Java 21+
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Validation
- JDA (Java Discord API)
- Hibernate
- PostgreSQL
- Flyway
- Maven

JDA sera utilisée comme couche de communication avec Discord.

Spring Boot sera responsable de la logique métier, de la configuration, de la base de données et de l’API REST.

### Frontend — phase 2

- Angular
- TypeScript
- Angular Material
- RxJS

L’application Angular communiquera uniquement avec l’API REST Spring Boot.

### Infrastructure

- Docker
- Docker Compose
- PostgreSQL
- Spring Boot
- Angular
- Nginx ou Caddy

Architecture cible :

```text
Internet
   │
   ├── Discord
   │      │
   │      ▼
   │   Bot JDA
   │      │
   │      ▼
   └── Spring Boot
          │
          ├── PostgreSQL
          │
          └── API REST
                │
                ▼
             Angular
```

---

## 4. Architecture applicative

La logique métier ne doit pas être placée directement dans le listener Discord.

À éviter :

```java
if (message.contains("c#")) {
    message.reply("Java > C#").queue();
}
```

Architecture recommandée :

```text
discord/
    DiscordBot.java
    DiscordMessageListener.java

controller/
    TriggerController.java
    ServerController.java

service/
    TriggerService.java
    MessageProcessingService.java
    ResponseService.java

repository/
    TriggerRepository.java
    TriggerResponseRepository.java
    DiscordServerRepository.java

entity/
    Trigger.java
    TriggerResponse.java
    DiscordServer.java
```

Flux de traitement :

```text
Message reçu
      │
      ▼
MessageProcessingService
      │
      ▼
Recherche des règles correspondantes
      │
      ▼
TriggerService
      │
      ▼
Sélection d'une réponse
      │
      ▼
Envoi via JDA
```

---

## 5. Gestion des déclencheurs

Chaque règle devra contenir au minimum :

- un nom ;
- un mot ou une expression recherchée ;
- un type de détection ;
- un statut actif / inactif ;
- un serveur Discord ;
- éventuellement un ou plusieurs salons ;
- un cooldown ;
- une ou plusieurs réponses associées.

Exemple :

```text
Nom :
Troll CSharp

Motif :
C#

Type :
CONTAINS

Actif :
Oui

Cooldown :
30 secondes
```

---

## 6. Types de détection

### EXACT

Le message doit être exactement égal au motif.

Exemple :

```text
C#
```

### CONTAINS

Le motif peut être contenu dans une phrase.

Exemples :

```text
J'aime C#
```

```text
C# c'est mieux que Java
```

### STARTS_WITH

Le message doit commencer par le motif.

### REGEX

Permettra des règles avancées via expressions régulières.

Exemple :

```regex
(?i)\bc#\b
```

Enum Java prévu :

```java
public enum TriggerType {
    EXACT,
    CONTAINS,
    STARTS_WITH,
    REGEX
}
```

---

## 7. Réponses multiples

Un déclencheur pourra posséder plusieurs réponses.

Exemple pour `C#` :

```text
🚨 C# détecté. Java demande officiellement un droit de réponse. ☕

C# ? Java avec une cravate Microsoft.

.NET vient d'entrer dans la conversation.

Une pensée pour la JVM qui doit regarder ça.
```

À chaque déclenchement, le bot pourra sélectionner une réponse aléatoire.

Relation :

```text
Trigger
   │
   ├── Response 1
   ├── Response 2
   ├── Response 3
   └── Response 4
```

---

## 8. Cooldown anti-spam

Chaque règle devra pouvoir définir un cooldown.

Exemple :

```text
C# détecté
→ réponse

5 secondes après :
C# détecté
→ ignoré

30 secondes après :
C# détecté
→ réponse
```

Le cooldown pourra être :

- global ;
- par serveur ;
- éventuellement par utilisateur dans une évolution future.

---

## 9. Protection contre les bots

Le bot devra ignorer les messages envoyés par les autres bots et ses propres messages.

Exemple avec JDA :

```java
if (event.getAuthor().isBot()) {
    return;
}
```

Cela évite notamment les boucles infinies entre bots.

---

## 10. Gestion des salons

Une règle pourra être applicable :

- à tous les salons ;
- uniquement à certains salons ;
- à tous les salons sauf une liste d’exclusion.

Exemple :

```text
Troll C#
    #general
    #dev
    #memes
```

Mais pas :

```text
#annonces
#administration
```

---

## 11. Gestion des serveurs Discord

L’architecture devra supporter plusieurs serveurs Discord.

Entité envisagée :

```text
DiscordServer

id
discordGuildId
name
enabled
createdAt
updatedAt
```

Relation :

```text
DiscordServer
     │
     ├── Trigger
     ├── Trigger
     └── Trigger
```

---

## 12. Modèle de données

### Table `discord_server`

```text
id
discord_guild_id
name
enabled
created_at
updated_at
```

### Table `trigger`

```text
id
name
pattern
type
enabled
cooldown_seconds
discord_server_id
created_at
updated_at
```

### Table `trigger_response`

```text
id
content
trigger_id
enabled
created_at
updated_at
```

Évolutions possibles :

```text
discord_channel
user
role
trigger_execution
command
setting
```

---

## 13. API REST

L’API REST sera prévue dès la V1 afin de préparer l’arrivée d’Angular.

Endpoints principaux :

```http
GET /api/triggers
GET /api/triggers/{id}
POST /api/triggers
PUT /api/triggers/{id}
DELETE /api/triggers/{id}
PATCH /api/triggers/{id}/enable
PATCH /api/triggers/{id}/disable
POST /api/triggers/{id}/responses
DELETE /api/responses/{id}
```

---

## 14. Exemple de création via API

```json
{
  "name": "Troll CSharp",
  "pattern": "C#",
  "type": "CONTAINS",
  "enabled": true,
  "cooldownSeconds": 30,
  "responses": [
    "🚨 C# détecté. Java demande officiellement un droit de réponse. ☕",
    "C# ? Java avec une cravate Microsoft.",
    ".NET vient d'entrer dans la conversation."
  ]
}
```

---

## 15. Interface Angular future

L’interface Angular devra proposer un dashboard d’administration.

Exemple :

```text
┌─────────────────────────────────────────┐
│              Discord Bot               │
├────────────┬────────────────────────────┤
│ Dashboard  │                            │
│ Serveurs   │   Déclencheurs actifs  12 │
│ Triggers   │   Réponses aujourd'hui 48 │
│ Logs       │   Serveurs connectés     1 │
│ Settings   │                            │
└────────────┴────────────────────────────┘
```

La page des déclencheurs pourra afficher :

| Nom | Déclencheur | Type | Cooldown | État |
|---|---|---|---:|---|
| Troll C# | `C#` | Contains | 30 s | ✅ |
| PHP | `PHP` | Contains | 60 s | ✅ |
| Windows | `Windows` | Contains | 20 s | ❌ |

Actions disponibles :

- ajouter ;
- modifier ;
- supprimer ;
- activer ;
- désactiver.

---

## 16. Formulaire Angular

Exemple de formulaire :

```text
Nom
[ Troll C#                         ]

Déclencheur
[ C#                               ]

Type
[ Contient                      ▼ ]

Cooldown
[ 30 ] secondes

Réponses

[ C# détecté. Java demande un droit de réponse. ]

[ C# ? Java avec une cravate Microsoft. ]

[ + Ajouter une réponse ]

                 [ Enregistrer ]
```

---

## 17. Logs

Un historique des déclenchements devra être prévu.

Exemple :

```text
12:42:31 | #dev | René | C# | Trigger Troll CSharp
12:45:12 | #general | John | PHP | Trigger PHP
12:46:09 | #dev | Alex | C# | Trigger Troll CSharp
```

---

## 18. Statistiques futures

L’interface pourra afficher :

- nombre de messages analysés ;
- nombre de réponses envoyées ;
- déclencheur le plus utilisé ;
- utilisateur déclenchant le plus le bot ;
- salon le plus actif ;
- nombre de déclenchements par jour.

---

## 19. Commandes Discord

Une évolution pourra ajouter des commandes slash.

Exemples :

```text
/help
/triggers
/trigger add
/trigger disable
/stats
/ping
/roast @user
```

---

## 20. Sécurité

Le token Discord ne devra jamais être stocké dans Git.

Configuration Spring Boot :

```yaml
discord:
  token: ${DISCORD_TOKEN}
```

Variable d’environnement :

```env
DISCORD_TOKEN=xxxxxxxxxxxxxxxx
```

Le fichier `.env` devra être ignoré :

```gitignore
.env
```

---

## 21. Docker

Le projet devra être dockerisable.

Exemple :

```yaml
services:

  backend:
    image: discord-bot
    environment:
      DISCORD_TOKEN: ${DISCORD_TOKEN}

  database:
    image: postgres:17
```

Architecture future :

```text
discord-bot-back
discord-bot-front
postgres
reverse-proxy
```

---

## 22. Découpage du projet

### Phase 1 — Bot minimal

Objectif :

```text
Discord → Spring Boot → JDA
```

Fonctionnalités :

- connexion Discord ;
- écoute des messages ;
- détection d’un trigger ;
- réponse automatique ;
- réponses aléatoires ;
- protection contre les bots.

Pas encore de base de données obligatoire.

### Phase 2 — Persistance

Ajout de :

- PostgreSQL ;
- Spring Data JPA ;
- Hibernate ;
- Flyway.

Les déclencheurs et réponses ne seront alors plus codés en dur.

### Phase 3 — API REST

Ajout des endpoints :

```text
/api/triggers
/api/responses
/api/servers
```

Documentation possible avec Swagger / OpenAPI.

### Phase 4 — Angular

Création de l’interface graphique d’administration.

```text
Angular
      │
      ▼
Spring REST API
      │
      ├── PostgreSQL
      │
      └── Discord / JDA
```

---

## 23. Évolutions possibles

- réponses avec GIF ;
- réactions emoji automatiques ;
- réponses aléatoires pondérées ;
- déclencheurs Regex ;
- déclencheurs par rôle ;
- déclencheurs par utilisateur ;
- horaires d’activation ;
- messages d’accueil ;
- attribution automatique de rôles ;
- commandes slash ;
- sondages ;
- citations ;
- statistiques ;
- système de niveaux ;
- blacklist de mots ;
- modération ;
- connexion à une IA ;
- génération de réponses via Ollama ;
- gestion de plusieurs serveurs Discord.

---

## 24. Structure cible du projet

```text
discord-bot/
│
├── backend/
│   ├── src/main/java/
│   │   └── fr/dwg/discordbot/
│   │       │
│   │       ├── config/
│   │       ├── controller/
│   │       ├── discord/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── repository/
│   │       ├── service/
│   │       └── DiscordBotApplication.java
│   │
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/
│   └── Angular
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## 25. Résultat attendu

La première version devra fournir un bot Discord fonctionnel capable de recevoir des messages, détecter des déclencheurs configurés et répondre automatiquement.

L’architecture devra être suffisamment modulaire pour permettre l’ajout ultérieur de PostgreSQL, d’une API REST complète et d’une interface Angular sans réécriture majeure du cœur du projet.
