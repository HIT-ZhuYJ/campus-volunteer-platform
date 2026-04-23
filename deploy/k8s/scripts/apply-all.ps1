$ErrorActionPreference = "Stop"
if (Get-Variable PSNativeCommandUseErrorActionPreference -ErrorAction SilentlyContinue) {
    $PSNativeCommandUseErrorActionPreference = $false
}

$ScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$RootDir = Resolve-Path (Join-Path $ScriptRoot "..\..\..")
$SecretFile = Join-Path $RootDir "deploy\k8s\cloud-demo\secret.yaml"
$IngressNginxManifest = "https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.15.1/deploy/static/provider/cloud/deploy.yaml"
$MetricsServerManifest = "https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml"
$UseMetricsServerInsecureTls = if ($env:CLOUD_DEMO_METRICS_SERVER_INSECURE_TLS) {
    $env:CLOUD_DEMO_METRICS_SERVER_INSECURE_TLS -ne "false"
} else {
    $true
}

if (-not (Test-Path $SecretFile)) {
    Write-Error "Missing $SecretFile. Create it from deploy/k8s/cloud-demo/secret.example.yaml first."
}

kubectl apply -f (Join-Path $RootDir "deploy\k8s\namespaces.yaml")
$IngressController = kubectl -n ingress-nginx get deployment/ingress-nginx-controller --ignore-not-found -o name 2>$null
if ([string]::IsNullOrWhiteSpace($IngressController)) {
    kubectl apply -f $IngressNginxManifest
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to install ingress-nginx from $IngressNginxManifest"
    }
} else {
    Write-Host "ingress-nginx controller already exists."
}
kubectl -n ingress-nginx rollout status deployment/ingress-nginx-controller --timeout=300s
if ($LASTEXITCODE -ne 0) {
    Write-Error "ingress-nginx controller did not become ready."
}

$MetricsServer = kubectl -n kube-system get deployment/metrics-server --ignore-not-found -o name 2>$null
if ([string]::IsNullOrWhiteSpace($MetricsServer)) {
    kubectl apply -f $MetricsServerManifest
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Failed to install metrics-server from $MetricsServerManifest"
    }
} else {
    Write-Host "metrics-server already exists."
}
if ($UseMetricsServerInsecureTls) {
    $MetricsServerArgs = kubectl -n kube-system get deployment/metrics-server -o jsonpath="{.spec.template.spec.containers[0].args}"
    if ($MetricsServerArgs -notmatch "--kubelet-insecure-tls") {
        $PatchFile = Join-Path $env:TEMP "cloud-demo-metrics-server-patch.json"
        '[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]' |
            Set-Content -Encoding ascii -NoNewline -LiteralPath $PatchFile
        kubectl -n kube-system patch deployment metrics-server --type=json --patch-file $PatchFile
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Failed to patch metrics-server for local kubelet TLS."
        }
    }
}
kubectl -n kube-system rollout status deployment/metrics-server --timeout=300s
if ($LASTEXITCODE -ne 0) {
    Write-Error "metrics-server did not become ready."
}

kubectl apply -f $SecretFile
kubectl apply -f (Join-Path $RootDir "deploy\k8s\cloud-demo\configmap.yaml")
kubectl apply -f (Join-Path $RootDir "deploy\k8s\cloud-demo\middleware.yaml")
kubectl apply -f (Join-Path $RootDir "deploy\k8s\cloud-demo\apps.yaml")
kubectl apply -f (Join-Path $RootDir "deploy\k8s\cloud-demo\edge-nginx.yaml")
kubectl apply -f (Join-Path $RootDir "deploy\k8s\cloud-demo\autoscaling.yaml")
kubectl apply -f (Join-Path $RootDir "deploy\k8s\cloud-demo\ingress.yaml")
kubectl apply -f (Join-Path $RootDir "deploy\k8s\observability\configmaps.yaml")
kubectl apply -f (Join-Path $RootDir "deploy\k8s\observability\stack.yaml")

Write-Host "Applied Kubernetes resources."
Write-Host "Next: run deploy/k8s/scripts/init-db.ps1 to import deploy/common/bootstrap-db.sql"
