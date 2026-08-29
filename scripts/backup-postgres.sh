#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
BACKUP_DIR="${BACKUP_DIR:-$ROOT/backups}"
CONTAINER="${POSTGRES_CONTAINER:-didibot-db}"
KEEP="${BACKUP_KEEP:-14}"

if [[ -f "$ROOT/.env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source "$ROOT/.env"
  set +a
fi

POSTGRES_DB="${POSTGRES_DB:-discord_bot}"
POSTGRES_USER="${POSTGRES_USER:-discord}"

if ! docker ps --format '{{.Names}}' | grep -qx "$CONTAINER"; then
  echo "Conteneur PostgreSQL introuvable: $CONTAINER" >&2
  echo "Démarre DidiBot ou passe POSTGRES_CONTAINER=..." >&2
  exit 1
fi

mkdir -p "$BACKUP_DIR"
stamp="$(date +%Y%m%d-%H%M%S)"
file="$BACKUP_DIR/didibot-postgres-$stamp.sql.gz"

docker exec "$CONTAINER" pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" | gzip > "$file"

if [[ "$KEEP" =~ ^[0-9]+$ ]] && [[ "$KEEP" -gt 0 ]]; then
  mapfile -t old < <(ls -1t "$BACKUP_DIR"/didibot-postgres-*.sql.gz 2>/dev/null | tail -n +"$((KEEP + 1))")
  if [[ ${#old[@]} -gt 0 ]]; then
    rm -f "${old[@]}"
  fi
fi

echo "Backup écrit : $file"
