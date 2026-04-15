#!/usr/bin/env bash
set -euo pipefail

docker compose -p shared -f compose.shared.yml up -d --build
