#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
CLUSTER_NAME="${CLOUD_DEMO_KIND_CLUSTER:-cloud-demo}"
KIND_CONFIG="${ROOT_DIR}/deploy/k8s/kind-config.yaml"
SECRET_EXAMPLE="${ROOT_DIR}/deploy/k8s/cloud-demo/secret.example.yaml"
SECRET_FILE="${ROOT_DIR}/deploy/k8s/cloud-demo/secret.yaml"
KIND_INGRESS_MANIFEST="https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.15.1/deploy/static/provider/kind/deploy.yaml"

if [[ -n "${KIND_EXE:-}" ]]; then
  KIND_BIN="${KIND_EXE}"
else
  KIND_BIN="kind"
fi

if ! "${KIND_BIN}" get clusters | grep -qx "${CLUSTER_NAME}"; then
  "${KIND_BIN}" create cluster --name "${CLUSTER_NAME}" --config "${KIND_CONFIG}"
else
  echo "kind cluster '${CLUSTER_NAME}' already exists."
fi

kubectl config use-context "kind-${CLUSTER_NAME}" >/dev/null

if ! kubectl -n ingress-nginx get deployment/ingress-nginx-controller >/dev/null 2>&1; then
  kubectl apply -f "${KIND_INGRESS_MANIFEST}"
fi
kubectl -n ingress-nginx rollout status deployment/ingress-nginx-controller --timeout=300s

IMAGES=(
  "cloud-demo/edge-nginx:latest"
  "cloud-demo/gateway-service:latest"
  "cloud-demo/user-service:latest"
  "cloud-demo/activity-service:latest"
  "cloud-demo/announcement-service:latest"
  "cloud-demo/feedback-service:latest"
  "cloud-demo/monitor-service:latest"
  "cloud-demo/mcp-service:latest"
)

for image in "${IMAGES[@]}"; do
  docker image inspect "${image}" >/dev/null
  "${KIND_BIN}" load docker-image --name "${CLUSTER_NAME}" "${image}"
done

if [[ ! -f "${SECRET_FILE}" ]]; then
  cp "${SECRET_EXAMPLE}" "${SECRET_FILE}"
  echo "Created deploy/k8s/cloud-demo/secret.yaml from secret.example.yaml for local kind deployment."
fi

bash "${ROOT_DIR}/deploy/k8s/scripts/apply-all.sh"
bash "${ROOT_DIR}/deploy/k8s/scripts/init-db.sh"

for deployment in edge-nginx gateway-service user-service activity-service announcement-service feedback-service monitor-service mcp-service; do
  kubectl -n cloud-demo rollout status "deployment/${deployment}" --timeout=300s
done

echo "kind cluster '${CLUSTER_NAME}' is ready."
echo "Open http://cloud-demo.local:18081/ after adding cloud-demo.local to hosts."
