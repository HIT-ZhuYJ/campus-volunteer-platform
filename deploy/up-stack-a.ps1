$ErrorActionPreference = 'Stop'

docker compose -p stack-a --env-file deploy/stack-a.env -f compose.stack.yml up -d --build
