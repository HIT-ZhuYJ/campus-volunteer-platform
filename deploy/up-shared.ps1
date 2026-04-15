$ErrorActionPreference = 'Stop'

docker compose -p shared -f compose.shared.yml up -d --build
