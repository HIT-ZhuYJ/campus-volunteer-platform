$ErrorActionPreference = 'Stop'

powershell -ExecutionPolicy Bypass -File deploy/up-shared.ps1
powershell -ExecutionPolicy Bypass -File deploy/up-stack-a.ps1
powershell -ExecutionPolicy Bypass -File deploy/up-stack-b.ps1
powershell -ExecutionPolicy Bypass -File deploy/up-edge.ps1
