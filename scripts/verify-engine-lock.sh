#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

if ! command -v git >/dev/null 2>&1; then
  echo "git is required" >&2
  exit 2
fi

git submodule update --init --recursive

status="$(git submodule status)"
printf '%s\n' "$status"

if printf '%s\n' "$status" | grep -Eq '^[+-U]'; then
  echo "One or more submodules are not at the recorded commit." >&2
  exit 1
fi

echo "All top-level engine submodules match their recorded commits."
