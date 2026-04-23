$ErrorActionPreference = "Stop"
if (Get-Variable PSNativeCommandUseErrorActionPreference -ErrorAction SilentlyContinue) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$ScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir = Resolve-Path (Join-Path $ScriptRoot "..\..\..")
$ClusterName = if ($env:CLOUD_DEMO_KIND_CLUSTER) { $env:CLOUD_DEMO_KIND_CLUSTER } else { "cloud-demo" }
$KindConfig = Join-Path $RootDir "deploy\k8s\kind-config.yaml"
$SecretExample = Join-Path $RootDir "deploy\k8s\cloud-demo\secret.example.yaml"
$SecretFile = Join-Path $RootDir "deploy\k8s\cloud-demo\secret.yaml"
$KindIngressManifest = "https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.15.1/deploy/static/provider/kind/deploy.yaml"

function Resolve-KindExe {
    if ($env:KIND_EXE -and (Test-Path $env:KIND_EXE)) {
        return (Resolve-Path $env:KIND_EXE).Path
    }
    $PathKind = Get-Command kind -ErrorAction SilentlyContinue
    if ($PathKind) {
        return $PathKind.Source
    }
    $LocalKind = Join-Path $RootDir ".tools\kind.exe"
    if (Test-Path $LocalKind) {
        return (Resolve-Path $LocalKind).Path
    }
    Write-Error "kind was not found. Install it or put kind.exe at .tools\kind.exe."
}

$KindExe = Resolve-KindExe

$ExistingClusters = & $KindExe get clusters
if ($ExistingClusters -notcontains $ClusterName) {
    & $KindExe create cluster --name $ClusterName --config $KindConfig
} else {
    Write-Host "kind cluster '$ClusterName' already exists."
}

kubectl config use-context "kind-$ClusterName" | Out-Null

$IngressController = kubectl -n ingress-nginx get deployment/ingress-nginx-controller --ignore-not-found -o name 2>$null
if ([string]::IsNullOrWhiteSpace($IngressController)) {
    kubectl apply -f $KindIngressManifest
}
kubectl -n ingress-nginx rollout status deployment/ingress-nginx-controller --timeout=300s
if ($LASTEXITCODE -ne 0) {
    Write-Error "ingress-nginx controller did not become ready."
}

$Images = @(
    "cloud-demo/edge-nginx:latest",
    "cloud-demo/gateway-service:latest",
    "cloud-demo/user-service:latest",
    "cloud-demo/activity-service:latest",
    "cloud-demo/announcement-service:latest",
    "cloud-demo/feedback-service:latest",
    "cloud-demo/monitor-service:latest",
    "cloud-demo/mcp-service:latest"
)

foreach ($Image in $Images) {
    docker image inspect $Image *> $null
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Missing Docker image $Image. Run 'docker compose build' from the repository root first."
    }
    & $KindExe load docker-image --name $ClusterName $Image
}

if (-not (Test-Path $SecretFile)) {
    Copy-Item $SecretExample $SecretFile
    Write-Host "Created deploy/k8s/cloud-demo/secret.yaml from secret.example.yaml for local kind deployment."
}

powershell -ExecutionPolicy Bypass -File (Join-Path $RootDir "deploy\k8s\scripts\apply-all.ps1")
powershell -ExecutionPolicy Bypass -File (Join-Path $RootDir "deploy\k8s\scripts\init-db.ps1")

$Deployments = @(
    "edge-nginx",
    "gateway-service",
    "user-service",
    "activity-service",
    "announcement-service",
    "feedback-service",
    "monitor-service",
    "mcp-service"
)
foreach ($Deployment in $Deployments) {
    kubectl -n cloud-demo rollout status "deployment/$Deployment" --timeout=300s
}

Write-Host "kind cluster '$ClusterName' is ready."
Write-Host "Open http://cloud-demo.local:18081/ after adding cloud-demo.local to hosts."
