$ErrorActionPreference = 'Stop'

docker compose -p edge -f compose.edge.yml up -d --build
