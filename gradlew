#!/usr/bin/env sh
set -e
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
else
  echo "Gradle is required but not installed." >&2
  exit 1
fi
