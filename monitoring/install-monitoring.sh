#!/usr/bin/env bash
# Installs Prometheus + Grafana + Alertmanager into the 'monitoring' namespace.
# Usage (on the machine with kubectl access):
#   GRAFANA_PASSWORD='YourStrongPass' bash monitoring/install-monitoring.sh
set -euo pipefail

: "${GRAFANA_PASSWORD:?Set GRAFANA_PASSWORD first, e.g. GRAFANA_PASSWORD='Grafana#2026' bash $0}"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

helm repo add prometheus-community https://prometheus-community.github.io/helm-charts >/dev/null 2>&1 || true
helm repo update >/dev/null

helm upgrade --install monitoring prometheus-community/kube-prometheus-stack \
  --namespace monitoring --create-namespace \
  -f "$SCRIPT_DIR/kube-prometheus-values.yaml" \
  --set grafana.adminPassword="$GRAFANA_PASSWORD" \
  --wait --timeout 10m

kubectl -n monitoring get pods,svc
echo
echo "Grafana:      http://<WORKER_EXTERNAL_IP>:30300  (user: admin)"
echo "Prometheus:   http://<WORKER_EXTERNAL_IP>:30090"
echo "Alertmanager: http://<WORKER_EXTERNAL_IP>:30093"
