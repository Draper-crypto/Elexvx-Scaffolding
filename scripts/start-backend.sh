#!/usr/bin/env bash

set -euo pipefail

# Resolve repository root regardless of where the script is invoked from.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${PROJECT_ROOT}/art-design-server"

# Allow overriding profile/credentials via positional args or environment variables.
PROFILE="${1:-${SPRING_PROFILES_ACTIVE:-dev}}"
DB_USER="${2:-${DB_USERNAME:-root}}"
DB_PASS="${3:-${DB_PASSWORD:-}}"

echo "Starting art-design-server (profile=${PROFILE}, db user=${DB_USER})"
SPRING_PROFILES_ACTIVE="${PROFILE}" \
  DB_USERNAME="${DB_USER}" \
  DB_PASSWORD="${DB_PASS}" \
  mvn -s maven-settings.xml spring-boot:run
