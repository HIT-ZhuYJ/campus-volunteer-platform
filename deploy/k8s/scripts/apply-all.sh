#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
SECRET_FILE="${ROOT_DIR}/deploy/k8s/cloud-demo/secret.yaml"
INGRESS_NGINX_MANIFEST="https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.15.1/deploy/static/provider/cloud/deploy.yaml"
METRICS_SERVER_MANIFEST="https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml"
METRICS_SERVER_INSECURE_TLS="${CLOUD_DEMO_METRICS_SERVER_INSECURE_TLS:-true}"

if [[ ! -f "${SECRET_FILE}" ]]; then
  echo "Missing ${SECRET_FILE}"
  echo "Create it from deploy/k8s/cloud-demo/secret.example.yaml first."
  exit 1
fi

kubectl apply -f "${ROOT_DIR}/deploy/k8s/namespaces.yaml"
if ! kubectl -n ingress-nginx get deployment/ingress-nginx-controller >/dev/null 2>&1; then
  kubectl apply -f "${INGRESS_NGINX_MANIFEST}"
else
  echo "ingress-nginx controller already exists."
fi
kubectl -n ingress-nginx rollout status deployment/ingress-nginx-controller --timeout=300s
if ! kubectl -n kube-system get deployment/metrics-server >/dev/null 2>&1; then
  kubectl apply -f "${METRICS_SERVER_MANIFEST}"
else
  echo "metrics-server already exists."
fi
if [[ "${METRICS_SERVER_INSECURE_TLS}" != "false" ]]; then
  METRICS_SERVER_ARGS="$(kubectl -n kube-system get deployment/metrics-server -o jsonpath='{.spec.template.spec.containers[0].args}')"
  if [[ "${METRICS_SERVER_ARGS}" != *"--kubelet-insecure-tls"* ]]; then
    PATCH_FILE="$(mktemp)"
    printf '%s' '[{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"}]' > "${PATCH_FILE}"
    kubectl -n kube-system patch deployment metrics-server --type=json --patch-file "${PATCH_FILE}"
    rm -f "${PATCH_FILE}"
  fi
fi
kubectl -n kube-system rollout status deployment/metrics-server --timeout=300s
kubectl apply -f "${SECRET_FILE}"
kubectl apply -f "${ROOT_DIR}/deploy/k8s/cloud-demo/configmap.yaml"
kubectl apply -f "${ROOT_DIR}/deploy/k8s/cloud-demo/middleware.yaml"
kubectl apply -f "${ROOT_DIR}/deploy/k8s/cloud-demo/apps.yaml"
kubectl apply -f "${ROOT_DIR}/deploy/k8s/cloud-demo/edge-nginx.yaml"
kubectl apply -f "${ROOT_DIR}/deploy/k8s/cloud-demo/autoscaling.yaml"
kubectl apply -f "${ROOT_DIR}/deploy/k8s/cloud-demo/ingress.yaml"
kubectl apply -f "${ROOT_DIR}/deploy/k8s/observability/configmaps.yaml"
kubectl apply -f "${ROOT_DIR}/deploy/k8s/observability/stack.yaml"

echo "Applied Kubernetes resources."
echo "Next: run deploy/k8s/scripts/init-db.sh to import deploy/common/bootstrap-db.sql"
