#!/usr/bin/env bash
set -euo pipefail

docker compose -p edge -f compose.edge.yml up -d --build
