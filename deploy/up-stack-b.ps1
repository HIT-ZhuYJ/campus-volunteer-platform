$ErrorActionPreference = 'Stop'

docker compose -p stack-b --env-file deploy/stack-b.env -f compose.stack.yml up -d --build
