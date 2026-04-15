#!/usr/bin/env bash
set -euo pipefail

bash deploy/up-shared.sh
bash deploy/up-stack-a.sh
bash deploy/up-stack-b.sh
bash deploy/up-edge.sh
